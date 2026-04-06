package restaurant.controller.admin;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import restaurant.entity.Discount;
import restaurant.service.CategoryService;
import restaurant.service.DiscountService;
import restaurant.service.ProductService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/discount")
@RequiredArgsConstructor
public class AdminDiscountController {
    private final DiscountService discountService;
    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping
    public String listDiscounts(Model model, HttpSession session) {
        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        if (branchId == null) return "redirect:/admin/home";

        model.addAttribute("discounts", discountService.getDiscountsByBranch(branchId));
        model.addAttribute("activePage", "discount");
        return "admin/admin-discount";
    }

    @PostMapping("/save")
    public String saveDiscount(@ModelAttribute Discount discount, HttpSession session, RedirectAttributes ra) {
        try {
            Integer branchId = (Integer) session.getAttribute("currentBranchId");
            discountService.saveDiscount(discount, branchId);
            ra.addFlashAttribute("success", "Đã lưu chương trình khuyến mãi!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/discount";
    }

    @GetMapping("/apply/{id}")
    public String showApplyForm(@PathVariable Long id, HttpSession session, Model model) {
        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        if (branchId == null) return "redirect:/admin/menu";

        Discount discount = discountService.getDiscountById(id);

        List<Long> appliedProductIds = discount.getProductDiscounts().stream()
                .filter(pd -> pd.getRestaurantProduct().getRestaurant().getRestaurantId().equals(branchId))
                .map(pd -> pd.getRestaurantProduct().getRestaurantProductId())
                .collect(Collectors.toList());

        model.addAttribute("discount", discount);
        model.addAttribute("branchProducts", productService.getAllProductsByBranch(branchId));
        model.addAttribute("appliedProductIds", appliedProductIds);
        model.addAttribute("categories", categoryService.getAllCategories());

        return "admin/discount-apply";
    }

    @PostMapping("/apply-process")
    public String applyDiscountProcess(@RequestParam Long discountId,
                                       @RequestParam(required = false) List<Long> restaurantProductIds,
                                       HttpSession session,
                                       RedirectAttributes ra) {
        try {
            Integer branchId = (Integer) session.getAttribute("currentBranchId");

            discountService.updateDiscountApplications(discountId, restaurantProductIds, branchId);

            ra.addFlashAttribute("success", "Cập nhật danh sách khuyến mãi thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/discount";
    }

    @GetMapping("/delete/{id}")
    public String deleteDiscount(@PathVariable Long id, RedirectAttributes ra) {
        try {
            discountService.deleteDiscount(id);
            ra.addFlashAttribute("success", "Đã xóa chiến dịch thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể xóa: " + e.getMessage());
        }
        return "redirect:/admin/discount";
    }
}