package restaurant.controller.user;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import restaurant.entity.Role;
import restaurant.entity.User;
import restaurant.repository.RoleRepository;
import restaurant.repository.UserRepository;
import restaurant.service.UserService;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/sign-up")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/sign-up";
    }

    @PostMapping("/sign-up")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email này đã được đăng ký tài khoản!");
            return "auth/sign-up";
        }

        if (userRepository.existsByPhone(user.getPhone())) {
            model.addAttribute("error", "Số điện thoại này đã được đăng ký tài khoản!");
            return "auth/sign-up";
        }

        Optional<Role> role = roleRepository.findByName("ROLE_USER");
        if (role.isEmpty()) {
            model.addAttribute("error", "Hệ thống chưa cấu hình quyền người dùng!");
            return "auth/sign-up";
        }

        try {
            userService.registerNewUser(user, role.orElseThrow(() -> new RuntimeException("Role not found")));
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra trong quá trình đăng ký!");
            return "auth/sign-up";
        }

        return "redirect:/sign-in?success";
    }

    @GetMapping("/sign-in")
    public String showSignInPage() {
        return "auth/sign-in";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/sign-in";
    }
}