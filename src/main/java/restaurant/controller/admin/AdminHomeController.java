package restaurant.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminHomeController {
    @GetMapping("admin/home")
    public String AdminHome(Model model) {
        model.addAttribute("page", "admin-home");
        return "admin/admin-home";
    }
}
