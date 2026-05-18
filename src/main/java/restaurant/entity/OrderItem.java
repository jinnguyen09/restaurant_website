package restaurant.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "restaurant_product_id", nullable = false)
    private Long restaurantProductId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "price_at_purchase", nullable = false, precision = 19, scale = 2)
    private BigDecimal priceAtPurchase;

    @Transient
    private boolean reviewed;
}