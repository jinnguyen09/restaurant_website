package restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String rankName;
    private int points;
    private LocalDateTime createdAt;

    private Long totalOrders;
    private Double totalSpent;
    private LocalDateTime lastOrderDate;

    private String avatar;
    private String roleName;
    private String restaurantName;
}