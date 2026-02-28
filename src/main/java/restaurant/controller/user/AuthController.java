package restaurant.controller.user;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import restaurant.entity.Role;
import restaurant.entity.User;
import restaurant.repository.RoleRepository;
import restaurant.repository.UserRepository;

import java.util.HashSet;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/sign-up")
    public String showSignUpForm(Model model) {

        model.addAttribute("user", new User());

        return "auth/sign-up";
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/sign-up")
    public String registerUser(@ModelAttribute("user") User user, Model model) {

        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email này đã được đăng ký tài khoản!");
            return "auth/sign-up";
        }

        if (userRepository.existsByPhone(user.getPhone())) {
            model.addAttribute("error", "Số điện thoại này đã được sử dụng!");
            return "auth/sign-up";
        }

        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            model.addAttribute("error", "Hệ thống chưa cấu hình quyền người dùng!");
            return "auth/sign-up";
        }

        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        user.getRoles().add(userRole);

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
        return "redirect:/sign-in?success";
    }

    @GetMapping("/sign-in")
    public String showSignInPage(Model model) {
        return "auth/sign-in";
    }

//    @PostMapping("/sign-in")
//    public String processSignIn(@RequestParam("identifier") String identifier,
//                                @RequestParam("password") String password,
//                                HttpSession session,
//                                Model model) {
//
//        User user = userRepository.findByEmailOrPhone(identifier, identifier);
//
//        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
//            session.setAttribute("loggedInUser", user);
//            return "redirect:/home";
//        }
//
//        model.addAttribute("error", "Thông tin đăng nhập hoặc mật khẩu không chính xác!");
//        return "auth/sign-in";
//    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }
}