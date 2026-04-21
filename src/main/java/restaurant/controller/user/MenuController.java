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
public class MenuController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping("/menu")
    public String Menu(
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpSession session,
            Model model) {

        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        if (branchId == null) {
            branchId = 1;
        }

        int pageSize = 9;

        Page<RestaurantProduct> productPage = productService.searchAndFilter(
                branchId,
                keyword,
                1,
                categoryId,
                page,
                pageSize
        );

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("keyword", keyword);

        model.addAttribute("categories", categoryService.getSubCategoriesByParentId(1));

        model.addAttribute("page", "menu");
        return "user/menu";
    }

    @GetMapping("/food-detail")
    public String FoodDetail(@RequestParam("id") Long productId, HttpSession session, Model model) {
        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        if (branchId == null) branchId = 1;

        model.addAttribute("product", productService.getProductDtoForBranch(branchId, productId));
        model.addAttribute("page", "menu");
        return "user/food-detail";
    }
}