package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import restaurant.entity.Discount;

import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    List<Discount> findByRestaurant_RestaurantIdOrderByDiscountIdDesc(Integer restaurantId);

    @Modifying
    @Transactional
    @Query("UPDATE Discount d SET d.status = 0 WHERE d.endDate < CURRENT_TIMESTAMP AND d.status = 1")
    void disableExpiredDiscounts();

    boolean existsByDiscountNameAndRestaurant_RestaurantId(String discountName, Integer restaurantId);

    @Query("SELECT d FROM Discount d WHERE d.restaurant.restaurantId = :branchId " +
            "AND d.status = 1 AND CURRENT_TIMESTAMP BETWEEN d.startDate AND d.endDate")
    List<Discount> findActiveDiscounts(@Param("branchId") Integer branchId);
}