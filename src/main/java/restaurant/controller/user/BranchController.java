package restaurant.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import restaurant.dto.CartItemDTO;
import restaurant.entity.User;
import restaurant.service.CartService;
import restaurant.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BranchController {

    private final CartService cartService;
    private final UserService userService;

    @GetMapping("/select-branch/{id}")
    public String selectBranch(@PathVariable Integer id,
                               HttpSession session,
                               HttpServletRequest request,
                               Principal principal) {

        String referer = request.getHeader("Referer");
        String targetUrl = (referer != null) ? referer : "/home";

        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            List<CartItemDTO> cartItems = cartService.getCartItemsByUserId(user.getUserId());

            if (!cartItems.isEmpty() && !cartItems.getFirst().getRestaurantId().equals(id)) {
                session.setAttribute("showConfirmClearCart", true);
                session.setAttribute("pendingBranchId", id);
                session.setAttribute("targetUrl", targetUrl);
                return "redirect:" + targetUrl;
            }
        }

        session.setAttribute("currentBranchId", id);
        return "redirect:" + targetUrl;
    }
}