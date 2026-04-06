package restaurant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "restaurant_products")
@Getter
@Setter
@NoArgsConstructor
public class RestaurantProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_product_id")
    private Long restaurantProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "is_available", columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isAvailable = true;

    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;

    @Column(name = "is_featured", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isFeatured = false;

    @OneToMany(mappedBy = "restaurantProduct")
    @org.hibernate.annotations.BatchSize(size = 10)
    private List<ProductDiscount> productDiscounts;

    @Transient
    private BigDecimal discountPrice;
}