package restaurant.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import restaurant.dto.CategoryDTO;
import restaurant.entity.Category;
import restaurant.repository.CategoryRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/category")
public class AdminCategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public String listCategories(Model model) {
        List<Category> allCategories = categoryRepository.findAll();

        List<Category> tableCategories = allCategories.stream()
                .filter(c -> c.getCategoryId() != 1 && c.getCategoryId() != 2)
                .toList();

        List<Category> rootParents = allCategories.stream()
                .filter(c -> c.getCategoryId() == 1 || c.getCategoryId() == 2)
                .toList();

        model.addAttribute("categories", tableCategories);
        model.addAttribute("parentCategories", rootParents);
        model.addAttribute("category", new Category());
        return "admin/admin-category";
    }

    @PostMapping("/save")
    public String saveCategory(@ModelAttribute("category") Category category,
                               @RequestParam(value = "parent", required = false) Integer parentId) {
        if (parentId != null) {
            Category parent = new Category();
            parent.setCategoryId(parentId);
            category.setParent(parent);
        } else {
            category.setParent(null);
        }
        categoryRepository.save(category);
        return "redirect:/admin/category?success";
    }

    @GetMapping("/edit/{id}")
    @ResponseBody
    public CategoryDTO getCategory(@PathVariable Integer id) {
        Category cat = categoryRepository.findById(id).orElse(null);
        if (cat == null) return null;

        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId(cat.getCategoryId());
        dto.setCategoryName(cat.getCategoryName());
        dto.setDescription(cat.getDescription());

        if (cat.getParent() != null) {
            dto.setParentId(cat.getParent().getCategoryId());
        }

        return dto;
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Integer id) {
        Category category = categoryRepository.findById(id).orElse(null);

        if (category == null || category.getParent() == null) {
            return "redirect:/admin/category?error=is_parent";
        }
        try {
            categoryRepository.deleteById(id);
            return "redirect:/admin/category?success=deleted";
        } catch (Exception e) {
            return "redirect:/admin/category?error=has_children";
        }
    }
}