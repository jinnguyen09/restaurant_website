package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import restaurant.entity.OrderItem;

// File riêng cho OrderItem
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
