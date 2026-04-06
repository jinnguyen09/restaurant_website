package restaurant.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminOrderController {
    @GetMapping("admin/order")
    public String AdminOrder(Model model) {
        model.addAttribute("page", "admin-order");
        return "admin/admin-order";
    }
}

