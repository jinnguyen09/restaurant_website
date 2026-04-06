package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import restaurant.entity.ProductDiscount;

import java.util.Optional;

@Repository
public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Long> {

    Optional<ProductDiscount> findByRestaurantProduct_RestaurantProductId(Long restaurantProductId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductDiscount pd WHERE pd.restaurantProduct.restaurantProductId = :rpId")
    void deleteByRestaurantProductId(@Param("rpId") Long restaurantProductId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductDiscount pd WHERE pd.discount.discountId = :discountId " +
            "AND pd.restaurantProduct.restaurant.restaurantId = :branchId")
    void deleteByDiscountAndBranch(@Param("discountId") Long discountId, @Param("branchId") Integer branchId);

    boolean existsByRestaurantProduct_RestaurantProductId(Long restaurantProductId);
}