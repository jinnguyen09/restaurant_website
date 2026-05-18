package restaurant.controller.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import restaurant.config.CustomUserDetails;
import restaurant.dto.ReviewRequest;
import restaurant.service.ReviewService;

import java.util.Map;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/submit")
    public String submitReview(@ModelAttribute ReviewRequest request,
                               @AuthenticationPrincipal CustomUserDetails currentUser,
                               RedirectAttributes redirectAttributes) {
        try {
            reviewService.submitReview(request, currentUser.getUserId());
            redirectAttributes.addFlashAttribute("success", "Cảm ơn bạn đã đánh giá món ăn!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/order/detail/" + request.getOrderId();
    }

    @PostMapping("/like/{reviewId}")
    @ResponseBody
    public ResponseEntity<?> toggleLike(@PathVariable Integer reviewId,
                                        @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Bạn cần đăng nhập để thực hiện thao tác này");
        }
        try {
            reviewService.toggleLike(reviewId, currentUser.getUserId());
            long newLikeCount = reviewService.getLikeCount(reviewId);
            return ResponseEntity.ok(Map.of("success", true, "newLikeCount", newLikeCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reply")
    public String submitReply(@RequestParam("parentId") Integer parentId,
                              @RequestParam("comment") String comment,
                              @RequestParam("productId") Long productId,
                              @AuthenticationPrincipal CustomUserDetails currentUser,
                              RedirectAttributes redirectAttributes) {
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để thực hiện phản hồi!");
            return "redirect:/login";
        }

        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isManager = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));

        if (!isAdmin && !isManager) {
            redirectAttributes.addFlashAttribute("error", "Chỉ có Quản trị viên hoặc Quản lý mới có quyền phản hồi đánh giá này!");
            return "redirect:/food-detail?id=" + productId;
        }

        try {
            reviewService.submitReply(parentId, comment, currentUser.getUserId());
            redirectAttributes.addFlashAttribute("success", "Phản hồi bình luận thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi gửi phản hồi!");
        }

        return "redirect:/food-detail?id=" + productId;
    }

    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id,
                               @RequestParam("productId") Long productId,
                               @AuthenticationPrincipal CustomUserDetails currentUser,
                               HttpSession session,
                               RedirectAttributes ra) {
        try {
            Integer branchId = (Integer) session.getAttribute("currentBranchId");
            String role = currentUser.getAuthorities().iterator().next().getAuthority();

            reviewService.deleteReview(id, currentUser.getUserId(), role, branchId);
            ra.addFlashAttribute("success", "Đã xóa bình luận");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/food-detail?id=" + productId;
    }

    @PostMapping("/update")
    public String updateReview(@RequestParam("reviewId") Integer reviewId,
                               @RequestParam("comment") String comment,
                               @RequestParam("rating") Integer rating,
                               @RequestParam("productId") Long productId,
                               @AuthenticationPrincipal CustomUserDetails currentUser,
                               RedirectAttributes ra) {
        try {
            reviewService.updateReview(reviewId, comment, rating, currentUser.getUserId());
            ra.addFlashAttribute("success", "Cập nhật đánh giá thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể cập nhật: " + e.getMessage());
        }
        return "redirect:/food-detail?id=" + productId;
    }
}