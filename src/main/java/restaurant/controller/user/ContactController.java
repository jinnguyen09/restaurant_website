package restaurant.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContactController {
    @GetMapping("/contact")
    public String Contact(Model model) {
        model.addAttribute("page", "contact");
        return "user/contact";
    }
}
