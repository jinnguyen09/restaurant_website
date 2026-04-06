package restaurant.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "users_roles")
@Data
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Role role;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public Integer getRestaurantId() {
        return this.restaurant != null ? this.restaurant.getRestaurantId() : null;
    }

    public void setRestaurantId(Integer restaurantId) {
        if (restaurantId == null) {
            this.restaurant = null;
            return;
        }
        if (this.restaurant == null) {
            this.restaurant = new Restaurant();
        }
        this.restaurant.setRestaurantId(restaurantId);
    }
}