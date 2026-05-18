package restaurant.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurant.dto.ReviewRequest;
import restaurant.entity.*;
import restaurant.repository.OrderItemRepository;
import restaurant.repository.ReviewLikeRepository;
import restaurant.repository.ReviewRepository;
import restaurant.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    public Double getAverageRating(Long rpId) {
        Double avg = reviewRepository.getAverageRatingByRestaurantProductId(rpId);
        return (avg != null) ? avg : 0.0;
    }

    public boolean isItemReviewed(Long orderItemId) {
        return reviewRepository.existsByOrderItemOrderItemId(orderItemId);
    }

    public Long getReviewCount(Long rpId) {
        Long count = reviewRepository.countReviewsByRestaurantProductId(rpId);
        return (count != null) ? count : 0L;
    }

    @Transactional
    public void submitReview(ReviewRequest request, Long userId) {
        OrderItem item = orderItemRepository.findById(request.getOrderItemId())
                .orElseThrow(() -> new EntityNotFoundException("Món ăn không tồn tại"));

        Order order = item.getOrder();

        if (!order.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("Bạn không có quyền đánh giá đơn hàng này");
        }

        if (!"PAID".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalStateException("Vui lòng thanh toán trước khi đánh giá");
        }

        if (LocalDateTime.now().isAfter(order.getCreatedAt().plusDays(3))) {
            throw new IllegalStateException("Đã quá thời hạn 3 ngày để gửi đánh giá");
        }

        if (isItemReviewed(request.getOrderItemId())) {
            throw new IllegalStateException("Món ăn này đã được đánh giá rồi");
        }

        Review review = Review.builder()
                .orderItem(item)
                .user(userRepository.getReferenceById(userId))
                .rating(request.getRating())
                .comment(request.getComment())
                .status(ReviewStatus.PUBLISHED)
                .build();

        reviewRepository.save(review);
    }

    @Transactional
    public void toggleLike(Integer reviewId, Long userId) {
        Optional<ReviewLike> existingLike = reviewLikeRepository
                .findByReview_ReviewIdAndUser_UserId(reviewId, userId);

        if (existingLike.isPresent()) {
            reviewLikeRepository.delete(existingLike.get());
        } else {
            ReviewLike newLike = ReviewLike.builder()
                    .review(reviewRepository.getReferenceById(Long.valueOf(reviewId)))
                    .user(userRepository.getReferenceById(userId))
                    .build();
            reviewLikeRepository.save(newLike);
        }
    }

    public long getLikeCount(Integer reviewId) {
        return reviewLikeRepository.countByReview_ReviewId(reviewId);
    }

    @Transactional
    public void submitReply(Integer parentId, String comment, Long userId) {
        Review parentReview = reviewRepository.getReferenceById(Long.valueOf(parentId));

        Review reply = Review.builder()
                .comment(comment)
                .parent(parentReview)
                .user(userRepository.getReferenceById(userId))
                .orderItem(null)
                .rating(5)
                .status(ReviewStatus.PUBLISHED)
                .createdAt(java.time.LocalDateTime.now())
                .build();

        reviewRepository.save(reply);
    }

    public List<Long> getLikedReviewIdsByUser(Long userId) {
        return reviewLikeRepository.findLikedReviewIdsByUserId(userId);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId, String userRole, Integer currentBranchId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bình luận"));

        boolean isOwner = review.getUser() != null && review.getUser().getUserId().equals(userId);
        boolean isAdmin = "ROLE_ADMIN".equals(userRole);

        boolean isManager = isIsManager(userRole, currentBranchId, review);

        if (isOwner || isAdmin || isManager) {

            if (review.getParent() != null) {
                Review parent = review.getParent();
                parent.getReplies().remove(review);
                review.setParent(null);
            }

            reviewRepository.delete(review);

        } else {
            throw new AccessDeniedException("Bạn không có quyền xóa bình luận này");
        }
    }

    private boolean isIsManager(String userRole, Integer currentBranchId, Review review) {
        if (!"ROLE_MANAGER".equals(userRole) || currentBranchId == null) {
            return false;
        }

        Integer reviewBranchId = null;

        if (review.getOrderItem() != null &&
                review.getOrderItem().getOrder() != null &&
                review.getOrderItem().getOrder().getRestaurant() != null) {

            reviewBranchId = review.getOrderItem().getOrder().getRestaurant().getRestaurantId();

        }
        else if (review.getParent() != null &&
                review.getParent().getOrderItem() != null &&
                review.getParent().getOrderItem().getOrder() != null &&
                review.getParent().getOrderItem().getOrder().getRestaurant() != null) {

            reviewBranchId = review.getParent().getOrderItem().getOrder().getRestaurant().getRestaurantId();
        }

        return reviewBranchId != null && reviewBranchId.equals(currentBranchId);
    }

    @Transactional
    public void updateReview(Integer reviewId, String newComment, Integer newRating, Long userId) {
        Review review = reviewRepository.findById(Long.valueOf(reviewId))
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đánh giá"));

        if (!review.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("Bạn không có quyền sửa đánh giá này!");
        }

        review.setComment(newComment);

        if (newRating != null && review.getParent() == null) {
            if (newRating < 1) newRating = 1;
            if (newRating > 5) newRating = 5;
            review.setRating(newRating);
        }
        reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public Page<Review> getReviewsByProduct(Long productId, Long currentUserId, int page, int size, String sortType) {
        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.findPublishedReviewsWithFilter(productId, currentUserId, sortType, pageable);
    }
}