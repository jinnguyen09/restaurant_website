package restaurant.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BranchController {

    @GetMapping("/select-branch/{id}")
    public String selectBranch(@PathVariable Integer id, HttpSession session, HttpServletRequest request) {
        session.setAttribute("currentBranchId", id);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "user/home");
    }
}