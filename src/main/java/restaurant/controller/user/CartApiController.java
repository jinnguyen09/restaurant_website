package restaurant.controller.user;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.ModelAndView;
import restaurant.config.CustomUserDetails;
import restaurant.dto.CartItemDTO;
import restaurant.entity.User;
import restaurant.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import restaurant.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartApiController {

    private final CartService cartService;
    private final UserService userService;

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @RequestBody Map<String, Object> payload,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Vui lòng đăng nhập!"
            ));
        }

        Long productId = Long.valueOf(payload.get("productId").toString());
        Integer quantity = payload.containsKey("quantity") ?
                Integer.parseInt(payload.get("quantity").toString()) : 1;

        try {
            cartService.addToCart(userDetails.getUserId(), productId, quantity);
            long count = cartService.getCartCount(userDetails.getUserId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "cartCount", count
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false));
        }
    }

    @GetMapping("/render")
    public ModelAndView renderCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        ModelAndView mav = new ModelAndView("layout/user/navbar :: cart_items_fragment");

        if (userDetails != null) {
            List<CartItemDTO> items = cartService.getCartItemsByUserId(userDetails.getUserId());
            mav.addObject("cartItems", items);
            mav.addObject("cartCount", items.size());
            mav.addObject("cartTotal", cartService.calculateTotal(items));
        }
        return mav;
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<?> removeFromCart(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            cartService.removeFromCart(userDetails.getUserId(), productId);
            long count = cartService.getCartCount(userDetails.getUserId());
            return ResponseEntity.ok(Map.of("success", true, "cartCount", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/clear-and-switch")
    public String clearAndSwitch(@RequestParam Integer branchId, HttpSession session, Principal principal) {
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            if (user != null) {
                cartService.clearCart(user.getUserId());
            }
        }

        session.setAttribute("currentBranchId", branchId);

        String targetUrl = (String) session.getAttribute("targetUrl");
        session.removeAttribute("showConfirmClearCart");
        session.removeAttribute("pendingBranchId");
        session.removeAttribute("targetUrl");

        return "redirect:" + (targetUrl != null ? targetUrl : "/menu");
    }

    @GetMapping("/cancel-switch")
    public ResponseEntity<?> cancelSwitch(HttpSession session) {
        session.removeAttribute("showConfirmClearCart");
        session.removeAttribute("pendingBranchId");
        return ResponseEntity.ok(Map.of("success", true));
    }
}