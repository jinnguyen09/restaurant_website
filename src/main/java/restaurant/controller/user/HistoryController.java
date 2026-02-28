package restaurant.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HistoryController {
    @GetMapping("/history")
    public String History(Model model) {
        return "user/history";
    }
}
