package restaurant.controller.admin;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import restaurant.entity.User;
import restaurant.service.RestaurantService;
import restaurant.service.RoleService;
import restaurant.service.UserService;

@Controller
@RequestMapping("/admin/customer")
public class AdminCustomerController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping
    public String AdminCustomer(Model model,
                                @RequestParam(name = "search", required = false) String search,
                                HttpSession session) {

        Integer branchId = (Integer) session.getAttribute("currentBranchId");

        model.addAttribute("page", "admin-customer");
        model.addAttribute("activePage", "customer");

        model.addAttribute("customers", userService.getCustomerStats(search, branchId));

        model.addAttribute("allRoles", roleService.getAllRoles());
        model.addAttribute("keyword", search);

        return "admin/admin-customer";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String showAddForm(Model model) {
        model.addAttribute("customer", new User());
        model.addAttribute("allRoles", roleService.getAllRoles());
        model.addAttribute("allRestaurants", restaurantService.getAllActiveBranches());
        model.addAttribute("title", "Thêm người dùng mới");
        return "admin/customer-form";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.getById(id);
        model.addAttribute("customer", user);
        model.addAttribute("allRoles", roleService.getAllRoles());
        model.addAttribute("allRestaurants", restaurantService.getAllActiveBranches());

        model.addAttribute("currentRoleId", userService.getCurrentRoleId(id));
        model.addAttribute("currentRestaurantId", userService.getCurrentRestaurantId(id));
        model.addAttribute("title", "Chỉnh sửa thông tin: " + user.getFullName());
        return "admin/customer-form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String saveCustomer(@ModelAttribute("customer") User user,
                               @RequestParam(value = "password", required = false) String password,
                               @RequestParam("roleId") Integer roleId,
                               @RequestParam(value = "restaurantId", required = false) Integer restaurantId,
                               Model model) {
        try {
            if (user.getUserId() == null) {
                userService.createNewUserFromAdmin(user, password, roleId, restaurantId);
            } else {
                userService.updateUserAdmin(user.getUserId(), user.getFullName(), user.getEmail(), user.getPhone(), roleId, restaurantId);
            }
            return "redirect:/admin/customer?success";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("allRoles", roleService.getAllRoles());
            model.addAttribute("allRestaurants", restaurantService.getAllActiveBranches());
            model.addAttribute("title", user.getUserId() == null ? "Thêm người dùng mới" : "Chỉnh sửa thông tin");

            model.addAttribute("currentRoleId", roleId);
            model.addAttribute("currentRestaurantId", restaurantId);

            return "admin/customer-form";
        }
    }
}