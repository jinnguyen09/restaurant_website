package restaurant.controller.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import restaurant.entity.RestaurantProduct;
import restaurant.entity.Review;
import restaurant.service.CategoryService;
import restaurant.service.ProductService;
import restaurant.service.ReviewService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import restaurant.config.CustomUserDetails;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MenuController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;

    @GetMapping("/menu")
    public String Menu(
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpSession session,
            Model model) {

        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        if (branchId == null) branchId = 1;

        int pageSize = 9;
        Page<RestaurantProduct> productPage = productService.searchAndFilter(
                branchId, keyword, 1, categoryId, page, pageSize
        );

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categories", categoryService.getSubCategoriesByParentId(1));
        model.addAttribute("page", "menu");

        return "user/menu";
    }

    @GetMapping("/food-detail")
    public String getFoodDetail(@RequestParam("id") Long productId,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "sort", defaultValue = "latest") String sort,
                                @AuthenticationPrincipal CustomUserDetails currentUser,
                                HttpSession session,
                                Model model) {

        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        if (branchId == null) branchId = 1;

        try {
            Long rpId = productService.getRestaurantProductId(branchId, productId);
            int pageSize = 5;

            Long currentUserId = (currentUser != null) ? currentUser.getUserId() : null;

            Page<Review> reviewPage = reviewService.getReviewsByProduct(rpId, currentUserId, page, pageSize, sort);
            Double avgRating = reviewService.getAverageRating(rpId);

            model.addAttribute("product", productService.getProductDtoForBranch(branchId, productId));
            model.addAttribute("restaurantProductId", rpId);
            model.addAttribute("averageRating", avgRating != null ? avgRating : 0.0);
            model.addAttribute("reviews", reviewPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", reviewPage.getTotalPages());
            model.addAttribute("totalItems", reviewPage.getTotalElements());
            model.addAttribute("currentSort", sort);

            if (currentUserId != null) {
                List<Long> likedReviewIds = reviewService.getLikedReviewIdsByUser(currentUserId);
                model.addAttribute("likedReviewIds", likedReviewIds);
            }

        } catch (Exception e) {
            System.err.println("Error fetching food details: " + e.getMessage());
            return "redirect:/menu";
        }

        model.addAttribute("page", "menu");
        return "user/food-detail";
    }
}