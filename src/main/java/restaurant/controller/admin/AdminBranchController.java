package restaurant.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import restaurant.entity.Restaurant;
import restaurant.service.RestaurantService; // Cần Inject thêm Service này

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminBranchController {

    private final RestaurantService restaurantService;

    @GetMapping("/change-branch/{id}")
    public String changeBranch(@PathVariable Integer id,
                               HttpSession session,
                               HttpServletRequest request) {

        Restaurant branch = restaurantService.getBranchById(id);

        if (branch != null) {
            session.setAttribute("currentBranchId", id);

            session.setAttribute("currentBranchName", branch.getName());

            System.out.println(">> Đã đổi sang chi nhánh: " + branch.getName() + " (ID: " + id + ")");
        }

        String referer = request.getHeader("Referer");

        if (referer != null && (referer.contains("/menu/add") || referer.contains("/menu/edit"))) {
            return "redirect:/admin/menu";
        }

        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }

        return "redirect:/admin/home";
    }
}