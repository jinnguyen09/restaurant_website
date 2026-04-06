package restaurant.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminReviewController {
    @GetMapping("admin/review")
    public String AdminReview(Model model) {
        model.addAttribute("page", "admin-review");
        return "admin/admin-review";
    }
}
