package restaurant.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    private TableEntity table;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "total_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "shipping_fee", precision = 19, scale = 2)
    private BigDecimal shippingFee;

    @Column(name = "amount_discounted", precision = 19, scale = 2)
    private BigDecimal amountDiscounted;

    @Column(name = "tax_amount", precision = 19, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "delivery_address", columnDefinition = "TEXT")
    private String deliveryAddress;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "order_type", nullable = false, length = 50)
    private String orderType;

    @Column(name = "points_redeemed")
    private Integer pointsRedeemed;

    @Column(name = "point_earned")
    private Integer pointEarned;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
}