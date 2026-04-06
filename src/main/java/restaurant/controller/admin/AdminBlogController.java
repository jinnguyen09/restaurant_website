package restaurant.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminBlogController {
    @GetMapping("admin/blog")
    public String AdminBlog(Model model) {
        model.addAttribute("page", "admin-blog");
        return "admin/admin-blog";
    }
}
