package restaurant.controller.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import restaurant.entity.Restaurant;
import restaurant.service.RestaurantService;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final RestaurantService restaurantService;

    @GetMapping({"/", "/home"})
    public String UserHome(Model model, HttpSession session) {
        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        if (branchId == null) {
            branchId = 1;
            session.setAttribute("currentBranchId", branchId);
        }

        Restaurant currentBranch = restaurantService.getRestaurantInfo(branchId);

        model.addAttribute("restaurant", currentBranch);
        model.addAttribute("page", "home");

        return "user/home";
    }
}