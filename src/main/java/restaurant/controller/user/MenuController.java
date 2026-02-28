package restaurant.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {
    @GetMapping("/menu")
    public String Menu(Model model) {
        model.addAttribute("page", "menu");
        return "user/menu";
    }

    @GetMapping("/food-detail")
    public String FoodDetail(Model model) {
        model.addAttribute("page", "menu");
        return "user/food-detail";
    }
}
