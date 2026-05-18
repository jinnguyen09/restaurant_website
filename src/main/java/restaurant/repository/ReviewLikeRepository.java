package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restaurant.entity.ReviewLike;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    Optional<ReviewLike> findByReview_ReviewIdAndUser_UserId(Integer reviewId, Long userId);

    long countByReview_ReviewId(Integer reviewId);

    void deleteByReview_ReviewIdAndUser_UserId(Integer reviewId, Long userId);

    @Query("SELECT rl.review.reviewId FROM ReviewLike rl WHERE rl.user.userId = :userId")
    List<Long> findLikedReviewIdsByUserId(@Param("userId") Long userId);}