package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import restaurant.entity.Order;
import restaurant.entity.OrderItem;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUser_UserIdOrderByCreatedAtDesc(Long userId);
}

