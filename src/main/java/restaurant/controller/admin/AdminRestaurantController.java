package restaurant.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import restaurant.entity.Restaurant;
import restaurant.service.FileService;
import restaurant.service.RestaurantService;

@Controller
@RequestMapping("/admin/restaurant")
@RequiredArgsConstructor
public class AdminRestaurantController {

    private final RestaurantService restaurantService;
    private final FileService fileService;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("restaurant", restaurantService.getAllBranches());
        return "admin/admin-restaurant";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Restaurant restaurant,
                       @RequestParam("avatarFile") MultipartFile avatarFile,
                       @RequestParam("imgFile") MultipartFile imgFile,
                       RedirectAttributes ra) {
        try {
            if (!avatarFile.isEmpty()) {
                fileService.deleteOldFile(restaurant.getRestaurantAvatar());
                String avatarUrl = fileService.saveAvatar(avatarFile);
                restaurant.setRestaurantAvatar(avatarUrl);
            }

            if (!imgFile.isEmpty()) {
                fileService.deleteOldFile(restaurant.getRestaurantImg());
                String imgUrl = fileService.saveImage(imgFile);
                restaurant.setRestaurantImg(imgUrl);
            }

            restaurantService.saveBranch(restaurant);
            ra.addFlashAttribute("success", "Lưu thông tin thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/restaurant/list";
    }
}