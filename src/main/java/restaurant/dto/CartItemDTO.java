package restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Long cartItemId;
    private Long productId;
    private Long restaurantProductId;
    private String productName;
    private String productImageUrl;
    private Double price;
    private Double originalPrice;
    private Integer quantity;
    private Double subTotal;
    private Integer stockQuantity;
    private Integer restaurantId;
}