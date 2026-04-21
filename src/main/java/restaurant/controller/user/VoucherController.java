package restaurant.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import restaurant.entity.User;
import restaurant.entity.UserVoucher;
import restaurant.service.UserService;
import restaurant.service.VoucherService;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class VoucherController {

    private final UserService userService;
    private final VoucherService voucherService;

    @GetMapping("/my-vouchers")
    public String myVouchers(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userService.findByEmail(principal.getName());

        List<UserVoucher> userVouchers = voucherService.getVouchersByUser(user);
        userVouchers.sort((a, b) -> {
            if (a.getUsedAt() == null && b.getUsedAt() != null) return -1;
            if (a.getUsedAt() != null && b.getUsedAt() == null) return 1;
            return 0;
        });

        model.addAttribute("userVouchers", userVouchers);
        return "user/my-vouchers";
    }
}