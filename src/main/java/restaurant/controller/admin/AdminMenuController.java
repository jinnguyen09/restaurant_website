package restaurant.controller.admin;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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
import org.springframework.data.domain.Page;

import java.io.IOException;

@Controller
@RequestMapping("/admin/menu")
@RequiredArgsConstructor
public class AdminMenuController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final FileService fileService;

    @GetMapping
    public String listProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpSession session, Model model) {

        Integer branchId = (Integer) session.getAttribute("currentBranchId");

        if (branchId == null) {
            return "redirect:/admin/home";
        }

        int pageSize = 6;

        Page<RestaurantProduct> productPage = productService.searchAndFilter(branchId, keyword, 1, categoryId, page, pageSize);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());

        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);

        model.addAttribute("categories", categoryService.getSubCategoriesByParentId(1));
        model.addAttribute("activePage", "menu");

        return "/admin/admin-menu";
    }

    @GetMapping("/add")
    public String showAddForm(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("currentBranchId") == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn phải chọn một chi nhánh trước khi thêm món ăn!");
            return "redirect:/admin/menu";
        }
        model.addAttribute("productDto", new ProductBranchDTO());
        model.addAttribute("categories", categoryService.getSubCategoriesByParentId(1));
        return "/admin/menu-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, HttpSession session, Model model) {
        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        if (branchId == null) return "redirect:/admin/menu";

        try {
            ProductBranchDTO dto = productService.getProductDtoForBranch(branchId, id);
            model.addAttribute("productDto", dto);
            model.addAttribute("categories", categoryService.getSubCategoriesByParentId(1));
            return "/admin/menu-form";
        } catch (Exception e) {
            return "redirect:/admin/menu";
        }
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("productDto") ProductBranchDTO productDto,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) throws IOException {

        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        if (branchId == null) return "redirect:/admin/menu";

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
        return "redirect:/admin/menu";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer branchId = (Integer) session.getAttribute("currentBranchId");

        if (branchId == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn chi nhánh!");
            return "redirect:/admin/menu";
        }

        try {
            Product product = productService.getProductById(id);

            if (product != null) {
                if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                    fileService.deleteOldFile(product.getImageUrl());
                }

                productService.removeProductFromBranch(branchId, id);
                redirectAttributes.addFlashAttribute("success", "Đã xóa món ăn và hình ảnh thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa: " + e.getMessage());
        }

        return "redirect:/admin/menu";
    }
}