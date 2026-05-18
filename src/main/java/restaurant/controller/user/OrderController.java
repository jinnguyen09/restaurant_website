package restaurant.controller.user;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import restaurant.dto.CartItemDTO;
import restaurant.dto.OrderRequest;
import restaurant.entity.Order;
import restaurant.entity.Restaurant;
import restaurant.entity.User;
import restaurant.entity.UserVoucher;
import restaurant.service.*;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final CartService cartService;
    private final UserService userService;
    private final OrderService orderService;
    private final VoucherService voucherService;
    private final ReviewService reviewService;

    @GetMapping("/checkout")
    public String checkout(Model model, Principal principal, HttpSession session) {
        if (principal == null) return "redirect:/login";

        User currentUser = userService.findByEmail(principal.getName());
        if (currentUser == null) return "redirect:/login";

        List<CartItemDTO> cartItems = cartService.getCartItemsByUserId(currentUser.getUserId());
        if (cartItems.isEmpty()) return "redirect:/menu";

        Integer selectedRestaurantId = cartItems.getFirst().getRestaurantId();
        session.setAttribute("currentBranchId", selectedRestaurantId);

        Restaurant branchConfig = userService.getRestaurantById(selectedRestaurantId);
        List<UserVoucher> availableVouchers = voucherService.getAvailableVouchersForUser(currentUser, selectedRestaurantId);

        model.addAttribute("orderRequest", new OrderRequest());

        model.addAttribute("branchConfig", branchConfig);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartService.calculateTotal(cartItems));
        model.addAttribute("cartCount", cartService.getCartCount(currentUser.getUserId()));
        model.addAttribute("availableVouchers", availableVouchers);

        return "user/checkout";
    }

    @PostMapping("/order/place")
    public String placeOrder(OrderRequest orderRequest,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";

        try {
            User currentUser = userService.findByEmail(principal.getName());

            Order savedOrder = orderService.placeOrder(orderRequest, currentUser);

            if ("TRANSFER".equals(orderRequest.getPaymentMethod())) {
                String paymentUrl = orderService.createPaymentLink(savedOrder);
                log.info("Redirecting to PayOS for Order ID: {}", savedOrder.getOrderId());
                return "redirect:" + paymentUrl;
            } else {
                log.info("Cash payment for Order ID: {}", savedOrder.getOrderId());
                return "redirect:/order/success/" + savedOrder.getOrderId();
            }

        } catch (Exception e) {
            log.error("Order/Payment failed for user {}: {}", principal.getName(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi thanh toán: " + e.getMessage());
            return "redirect:/checkout?error";
        }
    }

    @GetMapping("/order/success/{id}")
    public String orderSuccess(@PathVariable Long id, Model model) {
        orderService.updateOrderStatus(id, "PAID");
        model.addAttribute("orderId", id);
        return "user/order_success";
    }

    @GetMapping("/order/detail/{id}")
    public String orderDetail(@PathVariable Long id, Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        Order order = orderService.getOrderById(id);
        User currentUser = userService.findByEmail(principal.getName());

        if (!order.getUser().getUserId().equals(currentUser.getUserId())) {
            return "redirect:/error/403";
        }

        order.getOrderItems().forEach(item -> {
            boolean isReviewed = reviewService.isItemReviewed(item.getOrderItemId());
            item.setReviewed(isReviewed);
        });

        model.addAttribute("order", order);
        return "user/order_detail";
    }

    @GetMapping("/order/cancel-checkout/{id}")
    public String cancelFromCheckout(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        orderService.updateOrderStatus(id, "CANCELLED");
        redirectAttributes.addFlashAttribute("errorMessage", "Bạn đã hủy thanh toán đơn hàng #" + id);
        return "redirect:/checkout";
    }

    @GetMapping("/order/cancel/{id}")
    public String cancelOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        orderService.updateOrderStatus(id, "CANCELLED");
        redirectAttributes.addFlashAttribute("successMessage", "Đã hủy đơn hàng #" + id);
        return "redirect:/order/detail/" + id;
    }

    @GetMapping("/order/repay/{id}")
    public String repayOrder(@PathVariable Long id, Principal principal) throws Exception {
        if (principal == null) return "redirect:/login";

        Order order = orderService.getOrderById(id);

        User currentUser = userService.findByEmail(principal.getName());
        if (!order.getUser().getUserId().equals(currentUser.getUserId())) {
            return "redirect:/error/403";
        }

        if (!"PENDING".equals(order.getStatus())) {
            return "redirect:/order/detail/" + id;
        }

        String paymentUrl = orderService.createPaymentLink(order);
        return "redirect:" + paymentUrl;
    }

    @PostMapping("/payos-webhook")
    public ResponseEntity<?> handlePayOSWebhook(@RequestBody JsonNode payload) throws Exception {
        log.info("Nhận Webhook từ PayOS: {}", payload.toString());

        String webhookSignature = payload.get("signature").asText();
        JsonNode data = payload.get("data");

        Long orderId = data.get("orderCode").asLong();
        String desc = payload.get("desc").asText();

        if ("success".equals(desc)) {
            orderService.updateOrderStatus(orderId, "PAID");
            log.info("Cập nhật đơn hàng #{} thành PAID qua Webhook", orderId);
        }

        return ResponseEntity.ok().build();
    }
}