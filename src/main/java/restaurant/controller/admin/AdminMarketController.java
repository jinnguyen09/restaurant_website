package restaurant.controller.admin;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import restaurant.dto.ProductBranchDTO;
import restaurant.entity.Product;
import restaurant.entity.RestaurantProduct;
import restaurant.service.CategoryService;
import restaurant.service.FileService;
import restaurant.service.ProductService;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/admin/market")
@RequiredArgsConstructor
public class AdminMarketController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final FileService fileService;

    @GetMapping
    public String listMarketProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpSession session, Model model) {

        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        if (branchId == null) return "redirect:/admin/home";

        int pageSize = 6;
        Page<RestaurantProduct> productPage = productService.searchAndFilter(branchId, keyword, 2, categoryId, page, pageSize);

        Map<String, Long> stats = productService.getMarketStats(branchId);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());

        model.addAttribute("totalItemsCount", stats.get("totalItems"));
        model.addAttribute("inStock", stats.get("inStock"));
        model.addAttribute("outOfStock", stats.get("outOfStock"));
        model.addAttribute("totalInventory", stats.get("totalInventory"));

        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categories", categoryService.getSubCategoriesByParentId(2));
        model.addAttribute("activePage", "market");

        return "admin/admin-market";
    }

    @GetMapping("/add")
    public String showAddForm(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("currentBranchId") == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn chi nhánh trước!");
            return "redirect:/admin/market";
        }
        model.addAttribute("productDto", new ProductBranchDTO());
        model.addAttribute("categories", categoryService.getSubCategoriesByParentId(2));
        return "admin/market-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, HttpSession session, Model model) {
        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        if (branchId == null) return "redirect:/admin/market";

        try {
            ProductBranchDTO dto = productService.getProductDtoForBranch(branchId, id);
            model.addAttribute("productDto", dto);
            model.addAttribute("categories", categoryService.getSubCategoriesByParentId(2));
            return "admin/market-form";
        } catch (Exception e) {
            return "redirect:/admin/market";
        }
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("productDto") ProductBranchDTO productDto,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) throws IOException {

        Integer branchId = (Integer) session.getAttribute("currentBranchId");

        if (branchId == null) return "redirect:/admin/market";

        if (imageFile != null && !imageFile.isEmpty()) {
            if (productDto.getProductId() != null) {
                Product oldProduct = productService.getProductById(productDto.getProductId());
                if (oldProduct.getImageUrl() != null) {
                    fileService.deleteOldFile(oldProduct.getImageUrl());
                }
            }
            productDto.setImageUrl(fileService.saveAvatar(imageFile));
        } else if (productDto.getProductId() != null) {
            productDto.setImageUrl(productService.getProductById(productDto.getProductId()).getImageUrl());
        }

        try {
            productService.saveProductFromDto(productDto, branchId);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/market";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        if (branchId == null) return "redirect:/admin/market";

        try {
            Product product = productService.getProductById(id);
            if (product != null) {
                if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                    fileService.deleteOldFile(product.getImageUrl());
                }
                productService.removeProductFromBranch(branchId, id);
                redirectAttributes.addFlashAttribute("success", "Đã xóa sản phẩm thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa: " + e.getMessage());
        }

        return "redirect:/admin/market";
    }
}