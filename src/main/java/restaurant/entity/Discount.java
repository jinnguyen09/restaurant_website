package restaurant.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "discounts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long discountId;

    @Column(nullable = false)
    private String discountName;

    @Column(nullable = false)
    private String discountType;

    @Column(precision = 12, scale = 2)
    private BigDecimal discountValue;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer status;

    @OneToMany(mappedBy = "discount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDiscount> productDiscounts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
}