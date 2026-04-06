package restaurant.controller.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import restaurant.entity.Reservation;
import restaurant.entity.Restaurant;
import restaurant.entity.User;
import restaurant.service.ReservationService;
import restaurant.service.RestaurantService;
import restaurant.config.CustomUserDetails;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Controller
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final RestaurantService restaurantService;

    @GetMapping("/reservation")
    public String showReservationPage(Model model, HttpSession session) {
        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        if (branchId == null) {
            branchId = 1;
        }

        Restaurant restaurant = restaurantService.getRestaurantInfo(branchId);

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("page", "reservation");
        return "user/reservation";
    }

    @PostMapping("/reservation/submit")
    public String processReservation(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("time") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            @RequestParam("numberOfPeople") Integer numberOfPeople,
            @RequestParam(value = "description", required = false) String description,
            HttpSession session,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để đặt bàn!");
            return "redirect:/sign-in";
        }

        try {
            Integer branchId = (Integer) session.getAttribute("currentBranchId");
            if (branchId == null) branchId = 1;

            Reservation reservation = new Reservation();
            reservation.setReservationTime(LocalDateTime.of(date, time));
            reservation.setNumberOfPeople(numberOfPeople);
            reservation.setDescription(description);
            reservation.setStatus("PENDING");

            reservationService.saveReservationFromBranch(reservation, branchId);

            redirectAttributes.addFlashAttribute("success", "Đặt bàn thành công tại chi nhánh " +
                    restaurantService.getBranchNameById(branchId) + "!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/reservation";
    }
}