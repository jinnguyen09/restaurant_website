package restaurant.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import restaurant.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserId(Long userId);

    Optional<CartItem> findByUserIdAndRestaurantProductId(Long userId, Long productId);

    long countByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.userId = :userId AND c.restaurantProductId = :productId")
    void deleteByUserIdAndRestaurantProductId(Long userId, Long productId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}