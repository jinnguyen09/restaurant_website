package restaurant.controller.admin;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import restaurant.config.CustomUserDetails;
import restaurant.entity.Reservation;
import restaurant.entity.User;
import restaurant.service.ReservationService;
import restaurant.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin/reservation")
@RequiredArgsConstructor
public class AdminReservationController {

    private final ReservationService reservationService;
    private final UserService userService;

    @GetMapping
    public String listReservations(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "filter", defaultValue = "all") String filter,
            @RequestParam(value = "keyword", required = false) String keyword,
            HttpSession session, Model model,
            Authentication authentication) {

        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        if (branchId == null) return "redirect:/admin/home";

        int pageSize = 10;
        Page<Reservation> reservationPage = reservationService.searchReservations(branchId, filter, keyword, page, pageSize);

        List<User> internalUsers = userService.getInternalUsersByBranch(branchId);

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails currentUser) {
            model.addAttribute("currentUser", currentUser);
        }

        model.addAttribute("reservations", reservationPage.getContent());
        model.addAttribute("allCustomers", internalUsers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reservationPage.getTotalPages());
        model.addAttribute("totalItems", reservationPage.getTotalElements());

        model.addAttribute("filter", filter);
        model.addAttribute("keyword", keyword);

        model.addAttribute("activePage", "reservation");
        return "admin/admin-reservation";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam("id") Long id,
                               @RequestParam("status") String status,
                               HttpSession session,
                               RedirectAttributes ra) {
        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        try {
            reservationService.updateStatus(branchId, id, status);
            ra.addFlashAttribute("success", "Cập nhật trạng thái thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/reservation";
    }

    @GetMapping("/delete/{id}")
    public String deleteReservation(@PathVariable Long id,
                                    HttpSession session,
                                    RedirectAttributes ra) {
        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        try {
            reservationService.removeReservationFromBranch(branchId, id);
            ra.addFlashAttribute("success", "Đã xóa đơn đặt bàn!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/reservation";
    }

    @PostMapping("/save")
    public String saveReservation(@ModelAttribute("reservation") Reservation reservation,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        try {
            if (reservation.getReservationId() == null &&
                    (reservation.getUser() == null || reservation.getUser().getUserId() == null)) {
                throw new RuntimeException("Vui lòng chọn khách hàng!");
            }

            reservationService.saveReservationFromBranch(reservation, branchId);
            ra.addFlashAttribute("success", "Lưu thông tin thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/reservation";
    }
}