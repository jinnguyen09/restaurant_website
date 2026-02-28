package restaurant.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BlogController {
    @GetMapping("/blog")
    public String Blog(Model model) {
        model.addAttribute("page", "blog");
        return "user/blog";
    }

    @GetMapping("/post-detail")
    public String PostDetail(Model model) {
        model.addAttribute("page", "blog");
        return "user/post-detail";
    }
}
