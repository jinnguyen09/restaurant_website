package restaurant.controller.admin;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import restaurant.entity.Reservation;
import restaurant.entity.User;
import restaurant.service.ReservationService;
import restaurant.service.UserService;

@Controller
@RequestMapping("/admin/reservation")
@RequiredArgsConstructor
public class AdminReservationController {

    private final ReservationService reservationService;
    private final UserService userService;

    @GetMapping
    public String listReservations(
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpSession session, Model model) {

        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        if (branchId == null) {
            return "redirect:/admin/home";
        }

        int pageSize = 10;
        Page<Reservation> reservationPage = reservationService.getReservationsByBranch(branchId, page, pageSize);

        model.addAttribute("reservations", reservationPage.getContent());
        model.addAttribute("allCustomers", userService.getAllCustomers());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reservationPage.getTotalPages());
        model.addAttribute("totalItems", reservationPage.getTotalElements());

        model.addAttribute("activePage", "reservation");
        return "admin/admin-reservation";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam("id") Long id,
                               @RequestParam("status") String status,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        if (branchId == null) return "redirect:/admin/reservation";

        try {
            reservationService.updateStatus(branchId, id, status);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/reservation";
    }

    @GetMapping("/delete/{id}")
    public String deleteReservation(@PathVariable Long id,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {

        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        if (branchId == null) return "redirect:/admin/reservation";

        try {
            reservationService.removeReservationFromBranch(branchId, id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa đơn đặt bàn!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa: " + e.getMessage());
        }
        return "redirect:/admin/reservation";
    }

    // 4. (Tùy chọn) Lưu/Sửa nếu bạn dùng Form riêng
    @PostMapping("/save")
    public String saveReservation(@ModelAttribute("reservation") Reservation reservation,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        try {
            reservationService.saveReservationFromBranch(reservation, branchId);
            redirectAttributes.addFlashAttribute("success", "Lưu thông tin thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/reservation";
    }

    @PostMapping("/create")
    public String createReservation(@ModelAttribute Reservation reservation,
                                    @RequestParam("userId") Long userId,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        try {
            User user = new User();
            user.setUserId(userId);
            reservation.setUser(user);

            reservationService.saveReservationFromBranch(reservation, branchId);
            redirectAttributes.addFlashAttribute("success", "Thêm mới đơn đặt bàn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/reservation";
    }
}