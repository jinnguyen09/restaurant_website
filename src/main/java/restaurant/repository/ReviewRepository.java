package restaurant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restaurant.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT AVG(r.rating) FROM Review r " +
            "WHERE r.orderItem.restaurantProductId = :rpId AND r.parent IS NULL")
    Double getAverageRatingByRestaurantProductId(@Param("rpId") Long rpId);

    @Query("SELECT COUNT(r) FROM Review r " +
            "WHERE r.orderItem.restaurantProductId = :rpId AND r.parent IS NULL")
    Long countReviewsByRestaurantProductId(@Param("rpId") Long rpId);

    @EntityGraph(attributePaths = {"user", "user.rank", "orderItem", "orderItem.order"})
    @Query("SELECT r FROM Review r " +
            "WHERE r.orderItem.restaurantProductId = :rpId " +
            "AND r.status = 'PUBLISHED' " +
            "AND r.parent IS NULL")
    Page<Review> findPublishedReviewsByProductId(@Param("rpId") Long rpId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "user.rank", "orderItem", "orderItem.order"})
    @Query(value = "SELECT r FROM Review r " +
            "WHERE r.orderItem.restaurantProductId = :rpId " +
            "AND r.status = 'PUBLISHED' " +
            "AND r.parent IS NULL " +
            "ORDER BY " +
            "CASE WHEN :currentUserId IS NOT NULL AND r.user.userId = :currentUserId THEN 0 ELSE 1 END ASC, " +
            "CASE WHEN :sortType = 'rating_high' THEN r.rating END DESC, " +
            "CASE WHEN :sortType = 'rating_low' THEN r.rating END ASC, " +
            "r.createdAt DESC",
            countQuery = "SELECT COUNT(r) FROM Review r " +
                    "WHERE r.orderItem.restaurantProductId = :rpId " +
                    "AND r.status = 'PUBLISHED' " +
                    "AND r.parent IS NULL")
    Page<Review> findPublishedReviewsWithFilter(
            @Param("rpId") Long rpId,
            @Param("currentUserId") Long currentUserId,
            @Param("sortType") String sortType,
            Pageable pageable);

    boolean existsByOrderItemOrderItemId(Long orderItemId);
}