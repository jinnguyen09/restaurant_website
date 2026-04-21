package restaurant.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import restaurant.dto.CustomerDTO;
import restaurant.entity.Voucher;
import restaurant.service.RestaurantService;
import restaurant.service.VoucherService;
import restaurant.service.RankService;
import restaurant.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin/voucher")
@RequiredArgsConstructor
public class AdminVoucherController {

    private final VoucherService voucherService;
    private final RankService rankService;
    private final UserService userService;
    private final RestaurantService restaurantService;

    @GetMapping
    public String listVouchers(Model model, HttpSession session, HttpServletRequest request) {
        Integer branchId = (Integer) session.getAttribute("currentBranchId");

        boolean isAdmin = request.isUserInRole("ROLE_ADMIN");

        if (isAdmin) {
            model.addAttribute("vouchers", voucherService.getAllVouchers());
            model.addAttribute("restaurants", restaurantService.getAllActiveBranches());
        } else {
            model.addAttribute("vouchers", voucherService.getVouchersByBranch(branchId));
        }

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("ranks", rankService.getAllRanks());
        return "admin/admin-voucher";
    }

    @GetMapping("/search-users")
    @ResponseBody
    public ResponseEntity<List<CustomerDTO>> searchUsers(@RequestParam("keyword") String keyword) {
        List<CustomerDTO> users = userService.searchUsersForGift(keyword);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/save")
    public String saveVoucher(@ModelAttribute Voucher voucher,
                              @RequestParam(value = "restaurantIds", required = false) Integer restaurantId,
                              HttpSession session,
                              HttpServletRequest request,
                              RedirectAttributes ra) {
        try {
            boolean isAdmin = request.isUserInRole("ROLE_ADMIN");

            if (voucher.getVoucherId() == null) {
                if (isAdmin) {
                    List<Integer> ids = (restaurantId != null) ? List.of(restaurantId) : null;
                    voucherService.saveVoucherWithRestaurants(voucher, ids);
                } else {
                    Integer branchId = (Integer) session.getAttribute("currentBranchId");
                    voucher.setApplyType(Voucher.ApplyType.SPECIFIC);
                    voucherService.saveVoucherWithRestaurants(voucher, List.of(branchId));
                }
            } else {
                Voucher existing = voucherService.getVoucherById(voucher.getVoucherId());
                voucher.setApplyType(existing.getApplyType());
                voucherService.saveVoucher(voucher);
            }
            ra.addFlashAttribute("success", "Thao tác thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/voucher";
    }

    @GetMapping("/delete/{id}")
    public String deleteVoucher(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            voucherService.deleteVoucher(id);
            ra.addFlashAttribute("success", "Đã xóa voucher!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Bạn không thể xoá voucher sau khi đã được phát hành cho người dùng!");
        }
        return "redirect:/admin/voucher";
    }

    @PostMapping("/give")
    public String giveVoucher(@RequestParam("voucherId") Integer voucherId,
                              @RequestParam("type") String type,
                              @RequestParam(value = "target", required = false) String target,
                              @RequestParam(value = "rankTarget", required = false) String rankTarget,
                              RedirectAttributes ra) {
        try {
            switch (type) {
                case "USER":
                    voucherService.giveVoucherToUser(voucherId, target);
                    ra.addFlashAttribute("success", "Đã gửi tặng voucher cho: " + target);
                    break;

                case "RANK":
                    voucherService.giveVoucherByMembershipAsync(voucherId, rankTarget);
                    ra.addFlashAttribute("success", "Hệ thống đang xử lý tặng voucher cho hạng " + rankTarget + ". Vui lòng chờ trong giây lát!");
                    break;

                case "ALL":
                    voucherService.giveVoucherToAllAsync(voucherId);
                    ra.addFlashAttribute("success", "Đang bắt đầu tiến trình tặng voucher cho toàn bộ hệ thống...");
                    break;
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi hệ thống: " + e.getMessage());
        }
        return "redirect:/admin/voucher";
    }
}