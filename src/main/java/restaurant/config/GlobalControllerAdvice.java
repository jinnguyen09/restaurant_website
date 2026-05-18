package restaurant.config;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import restaurant.entity.Restaurant;
import restaurant.repository.NotificationRepository;
import restaurant.service.RestaurantService;
import restaurant.service.CartService; // 1. Import CartService
import restaurant.dto.CartItemDTO; // Import DTO

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final RestaurantService restaurantService;
    private final NotificationRepository notificationRepository;
    private final CartService cartService;

    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpSession session, Authentication authentication) {

        List<Restaurant> allBranches = restaurantService.getAllActiveBranches();
        model.addAttribute("branches", allBranches);
        model.addAttribute("allBranches", allBranches);

        boolean isAdmin = false;
        Integer userHardcodedBranchId = null;
        CustomUserDetails userDetails = null;

        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {

            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                userDetails = (CustomUserDetails) principal;
                Long userId = userDetails.getUserId();

                model.addAttribute("userPoints", userDetails.getPoints());
                model.addAttribute("topNotifications", notificationRepository.findAllByUserId(userId));
                model.addAttribute("unreadCount", notificationRepository.countUnreadNotifications(userId));

                List<CartItemDTO> cartItems = cartService.getCartItemsByUserId(userId);
                model.addAttribute("cartItems", cartItems);
                model.addAttribute("cartCount", cartItems.size());
                model.addAttribute("cartTotal", cartService.calculateTotal(cartItems));

                isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                model.addAttribute("isAdmin", isAdmin);

                userHardcodedBranchId = userDetails.getRestaurantId();
            }
        } else {
            model.addAttribute("cartItems", new ArrayList<>());
            model.addAttribute("cartCount", 0);
            model.addAttribute("cartTotal", 0.0);
        }

        Integer finalBranchId = (Integer) session.getAttribute("currentBranchId");

        if (isAdmin) {
            if (finalBranchId == null && !allBranches.isEmpty()) {
                finalBranchId = allBranches.getFirst().getRestaurantId();
                session.setAttribute("currentBranchId", finalBranchId);
            }
        } else if (userHardcodedBranchId != null) {
            finalBranchId = userHardcodedBranchId;
            session.setAttribute("currentBranchId", finalBranchId);
        } else {
            if (finalBranchId == null && !allBranches.isEmpty()) {
                finalBranchId = allBranches.getFirst().getRestaurantId();
                session.setAttribute("currentBranchId", finalBranchId);
            }
        }

        if (finalBranchId != null) {
            model.addAttribute("currentBranch", restaurantService.getBranchById(finalBranchId));
        }
    }
}