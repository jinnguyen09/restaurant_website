package restaurant.controller.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import restaurant.entity.RestaurantProduct;
import restaurant.service.CategoryService;
import restaurant.service.ProductService;

@Controller
@RequiredArgsConstructor
public class MarketController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping("/market")
    public String viewMarket(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpSession session, Model model) {

        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        if (branchId == null) {
            branchId = 1;
        }

        int pageSize = 9;

        Page<RestaurantProduct> productPage = productService.searchAndFilter(
                branchId, keyword, 2, categoryId, page, pageSize);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);

        model.addAttribute("categories", categoryService.getSubCategoriesByParentId(2));
        model.addAttribute("page", "market");
        return "user/market";
    }
}