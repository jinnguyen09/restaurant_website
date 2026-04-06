package restaurant.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_discounts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productDiscountsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_product_id", nullable = false)
    private RestaurantProduct restaurantProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id", nullable = false)
    private Discount discount;

    @Column(name = "special_price", precision = 12, scale = 2)
    private BigDecimal specialPrice;
}