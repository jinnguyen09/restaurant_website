package restaurant.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "restaurant_id", nullable = false)
    private Integer restaurantId;

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;

    @Column(name = "payment_status", nullable = false, length = 50)
    private String paymentStatus;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}