package restaurant.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderRequest {
    private Integer restaurantId;
    private Integer voucherId;
    private String orderType;
    private String deliveryAddress;
    private String orderNotes;
    private BigDecimal shippingFee;
    private BigDecimal taxAmount;
    private BigDecimal totalPrice;
    private String paymentMethod;
    private Integer pointsUsed;
}