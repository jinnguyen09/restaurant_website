package restaurant.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminTableController {
    @GetMapping("admin/table")
    public String AdminTable(Model model) {
        model.addAttribute("page", "admin-table");
        return "admin/admin-table";
    }
}