package restaurant.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminNotificationController {
    @GetMapping("admin/notification")
    public String AdminBlog(Model model) {
        model.addAttribute("page", "admin-notification");
        return "admin/admin-notification";
    }
}
