package restaurant.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import restaurant.service.OrderService;
import vn.payos.PayOS;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PayOSWebhookController {

    private final OrderService orderService;
    private final PayOS payOS;

    @PostMapping("/payos-webhook")
    public ResponseEntity<?> handlePayOSWebhook(@RequestBody Webhook webhookBody) {
        try {
            WebhookData data = payOS.verifyPaymentWebhookData(webhookBody);

            Long orderCode = data.getOrderCode();

            if ("00".equals(webhookBody.getCode())) {
                orderService.updateOrderStatus(orderCode, "PAID");
                log.info("Webhook: Đơn hàng #{} đã thanh toán thành công", orderCode);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
