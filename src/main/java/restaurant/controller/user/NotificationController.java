package restaurant.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotificationController {
    @GetMapping("/notification")
    public String Notification(Model model) {
        return "user/notification";
    }
}
