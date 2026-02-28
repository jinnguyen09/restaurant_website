package restaurant.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PointController {
    @GetMapping("/point")
    public String index(Model model) {
        return "user/point";
    }
}
