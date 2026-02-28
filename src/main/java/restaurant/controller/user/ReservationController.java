package restaurant.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReservationController {
    @GetMapping("/reservation")
    public String Reservation(Model model) {
        model.addAttribute("page", "reservation");
        return "user/reservation";
    }
}
