package restaurant.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "vouchers")
@Data
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voucher_id")
    private Integer voucherId;

    @Column(name = "voucher_name", nullable = false, length = 100)
    private String voucherName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "min_order_value", precision = 10, scale = 2)
    private BigDecimal minOrderValue = BigDecimal.ZERO;

    public Integer getRemainingUsages() {
        if (this.usageLimit == null) return 0;
        return this.usageLimit;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "apply_type", nullable = false)
    private ApplyType applyType = ApplyType.ALL;
    public enum ApplyType {
        ALL,
        SPECIFIC
    }

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoucherRestaurant> applicableRestaurants;
}