package restaurant.controller.user;

import lombok.RequiredArgsConstructor; // Đảm bảo có import này
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import restaurant.entity.Order;
import restaurant.entity.User;
import restaurant.service.OrderService;
import restaurant.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HistoryController {

    private final UserService userService;
    private final OrderService orderService;

    @GetMapping("/history")
    public String orderHistory(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User currentUser = userService.findByEmail(principal.getName());

        List<Order> orders = orderService.getOrdersByUserId(currentUser.getUserId());
        model.addAttribute("orders", orders);

        return "user/history";
    }
}