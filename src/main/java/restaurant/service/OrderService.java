package restaurant.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurant.dto.CartItemDTO;
import restaurant.dto.OrderRequest;
import restaurant.entity.*;
import restaurant.repository.*;
import vn.payos.PayOS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.JsonNode;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Formatter;
import java.nio.charset.StandardCharsets;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final UserService userService;
    private final RestaurantRepository restaurantRepository;
    private final VoucherRepository voucherRepository;
    private final PayOS payOS;
    private final PaymentRepository paymentRepository;

    @Value("${payos.client-id}")
    private String clientId;

    @Value("${payos.api-key}")
    private String apiKey;

    @Value("${payos.checksum-key}")
    private String checksumKey;

    @Transactional
    public Order placeOrder(OrderRequest request, User user) {
        List<CartItemDTO> cartItems = cartService.getCartItemsByUserId(user.getUserId());
        if (cartItems.isEmpty()) throw new RuntimeException("Giỏ hàng trống!");

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà hàng!"));

        // 1. Tính Voucher giảm giá
        Voucher voucher = null;
        BigDecimal voucherDiscount = BigDecimal.ZERO;
        if (request.getVoucherId() != null) {
            voucher = voucherRepository.findById(request.getVoucherId()).orElse(null);
            if (voucher != null) {
                voucherDiscount = voucher.getDiscountAmount();
            }
        }

        // 2. Tính Giảm giá từ điểm quy đổi
        BigDecimal pointDiscount = BigDecimal.ZERO;
        if (request.getPointsUsed() != null && request.getPointsUsed() > 0) {
            if (user.getPoints() < request.getPointsUsed()) {
                throw new RuntimeException("Bạn không đủ điểm tích lũy!");
            }
            // Công thức: Số điểm * Tỉ lệ quy đổi của chi nhánh
            pointDiscount = BigDecimal.valueOf(request.getPointsUsed())
                    .multiply(restaurant.getPointRedemptionRate());
        }

        BigDecimal subTotal = BigDecimal.valueOf(cartService.calculateTotal(cartItems));
        BigDecimal shipping = request.getShippingFee() != null ? request.getShippingFee() : BigDecimal.ZERO;
        BigDecimal tax = request.getTaxAmount() != null ? request.getTaxAmount() : BigDecimal.ZERO;

        // 3. Tính Tổng tiền cuối cùng (Trừ cả voucher và điểm)
        BigDecimal totalDiscount = voucherDiscount.add(pointDiscount);
        BigDecimal finalTotal = subTotal.add(shipping).add(tax).subtract(totalDiscount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) finalTotal = BigDecimal.ZERO;

        Order order = Order.builder()
                .user(user)
                .restaurant(restaurant)
                .voucher(voucher)
                .deliveryAddress(request.getDeliveryAddress())
                .status("PENDING")
                .orderType(request.getOrderType())
                .shippingFee(shipping)
                .taxAmount(tax)
                .amountDiscounted(totalDiscount)
                .pointsRedeemed(request.getPointsUsed() != null ? request.getPointsUsed() : 0)
                .totalPrice(finalTotal)
                .pointEarned((int) (finalTotal.doubleValue() / 1000))
                .build();

        Order savedOrder = orderRepository.save(order);

        // 4. Trừ điểm của người dùng trong Database
        if (request.getPointsUsed() != null && request.getPointsUsed() > 0) {
            user.setPoints(user.getPoints() - request.getPointsUsed());
            // UserService sẽ tự động lưu khi kết thúc @Transactional hoặc gọi explicit save
        }

        // 5. Khởi tạo Payment
        Payment payment = Payment.builder()
                .orderId(savedOrder.getOrderId())
                .restaurantId(restaurant.getRestaurantId())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus("PENDING")
                .build();
        paymentRepository.save(payment);

        // 6. Lưu OrderItems
        for (CartItemDTO item : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .restaurantProductId(item.getRestaurantProductId())
                    .itemName(item.getProductName())
                    .quantity(item.getQuantity())
                    .priceAtPurchase(BigDecimal.valueOf(item.getPrice()))
                    .build();
            orderItemRepository.save(orderItem);
        }

        cartItemRepository.deleteByUserId(user.getUserId());

        userService.addPoints(user.getUserId(), savedOrder.getPointEarned());

        return savedOrder;
    }

    public String createPaymentLink(Order order) throws Exception {
        String baseUrl = "http://localhost:8080";
        long orderCode = order.getOrderId();
        int amount = order.getTotalPrice().intValue();
        String description = "PH" + orderCode;
        String returnUrl = baseUrl + "/order/success/" + orderCode;
        String cancelUrl = baseUrl + "/order/cancel-checkout/" + order.getOrderId();

        String rawData = "amount=" + amount +
                "&cancelUrl=" + cancelUrl +
                "&description=" + description +
                "&orderCode=" + orderCode +
                "&returnUrl=" + returnUrl;

        String signature = hmacEncode(checksumKey, rawData);

        String jsonBody = String.format(
                "{\"orderCode\":%d,\"amount\":%d,\"description\":\"%s\",\"cancelUrl\":\"%s\",\"returnUrl\":\"%s\",\"signature\":\"%s\"}",
                orderCode, amount, description, cancelUrl, returnUrl, signature
        );

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-client-id", clientId);
        headers.set("x-api-key", apiKey);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                    "https://api-merchant.payos.vn/v2/payment-requests",
                    entity,
                    JsonNode.class
            );

            log.info("PayOS Response: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode body = response.getBody();
                if ("00".equals(body.get("code").asText())) {
                    return body.get("data").get("checkoutUrl").asText();
                } else {
                    throw new RuntimeException("PayOS Error: " + body.get("desc").asText());
                }
            } else {
                throw new RuntimeException("Lỗi kết nối HTTP: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Chi tiết lỗi gọi PayOS: {}", e.getMessage());
            throw new Exception("Không thể tạo liên kết thanh toán: " + e.getMessage());
        }
    }

    private String hmacEncode(String key, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] bytes = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hash = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hash.append('0');
            hash.append(hex);
        }
        return hash.toString();
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng #" + orderId));
    }

    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        Order order = getOrderById(orderId);

        if ("PAID".equals(order.getStatus()) && "PAID".equals(status)) {
            return;
        }

        order.setStatus(status);
        orderRepository.save(order);

        if ("PAID".equals(status)) {
            paymentRepository.findByOrderId(orderId).ifPresent(p -> {
                p.setPaymentStatus("COMPLETED");
                p.setPaidAt(java.time.LocalDateTime.now());
                paymentRepository.save(p);
            });
        } else if ("CANCELLED".equals(status)) {
            paymentRepository.findByOrderId(orderId).ifPresent(p -> {
                p.setPaymentStatus("FAILED");
                paymentRepository.save(p);
            });
        }
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findAllByUser_UserIdOrderByCreatedAtDesc(userId);
    }

}