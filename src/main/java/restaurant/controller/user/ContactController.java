package restaurant.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import restaurant.service.RestaurantService;

@Controller
@RequiredArgsConstructor
public class ContactController {

    private final RestaurantService restaurantService;

    @GetMapping("/contact")
    public String showContactPage(Model model) {
        model.addAttribute("page", "contact");
        return "user/contact";
    }
}