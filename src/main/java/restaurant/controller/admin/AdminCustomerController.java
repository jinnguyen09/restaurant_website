package restaurant.controller.admin;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import restaurant.entity.User;
import restaurant.service.RestaurantService;
import restaurant.service.RoleService;
import restaurant.service.UserService;
import restaurant.entity.Role;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/customer")
public class AdminCustomerController {

    @Autowired private UserService userService;
    @Autowired private RoleService roleService;
    @Autowired private RestaurantService restaurantService;

    @GetMapping
    public String listCustomers(Model model,
                                @RequestParam(name = "search", required = false) String search) {

        model.addAttribute("customers", userService.getCustomerStats(search));

        model.addAttribute("keyword", search);
        model.addAttribute("activePage", "customer");
        return "admin/admin-customer";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public String showAddForm(Model model, Authentication authentication, HttpSession session) {
        model.addAttribute("customer", new User());
        model.addAttribute("title", "Thêm người dùng mới");

        prepareFormModel(model, authentication, session, null, null);

        return "admin/user-form";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication, HttpSession session) {
        User user = userService.getById(id);
        model.addAttribute("customer", user);
        model.addAttribute("title", "Chỉnh sửa thông tin: " + user.getFullName());

        Integer currentRoleId = userService.getCurrentRoleId(id);
        Integer currentResId = userService.getCurrentRestaurantId(id);

        prepareFormModel(model, authentication, session, currentRoleId, currentResId);
        return "admin/user-form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public String saveCustomer(@ModelAttribute("customer") User user,
                               @RequestParam(value = "password", required = false) String password,
                               @RequestParam(value = "roleId", required = false) Integer roleId,
                               @RequestParam(value = "restaurantId", required = false) Integer restaurantId,
                               @RequestParam(value = "isStaffPage", required = false) Boolean isStaffPage,
                               Authentication authentication,
                               Model model, HttpSession session) {
        try {
            User loggedInUser = userService.getByEmail(authentication.getName());
            Long currentUserId = loggedInUser.getUserId();

            if (user.getUserId() != null && user.getUserId().equals(currentUserId)) {
                Integer oldRoleId = userService.getCurrentRoleId(currentUserId);
                Integer oldResId = userService.getCurrentRestaurantId(currentUserId);

                if (roleId == null) roleId = oldRoleId;
                if (restaurantId == null) restaurantId = oldResId;

                if (!java.util.Objects.equals(roleId, oldRoleId)) {
                    throw new RuntimeException("Bạn không thể tự thay đổi quyền hạn của chính mình!");
                }
                if (!java.util.Objects.equals(restaurantId, oldResId)) {
                    throw new RuntimeException("Bạn không thể tự chuyển chi nhánh của chính mình!");
                }
            }

            if (Boolean.TRUE.equals(isStaffPage)) {
                if (user.getUserId() != null) {
                    Integer targetUserRoleId = userService.getCurrentRoleId(user.getUserId());
                    if (java.util.Objects.equals(targetUserRoleId, 2) && !hasRole(authentication)) {
                        throw new RuntimeException("Bạn không có quyền chỉnh sửa tài khoản Quản trị viên!");
                    }
                }

                if (!hasRole(authentication) && java.util.Objects.equals(roleId, 2)) {
                    throw new RuntimeException("Bạn không có quyền gán quyền quản trị viên tổng!");
                }

                if (java.util.Objects.equals(roleId, 1)) {
                    throw new RuntimeException("Không thể gán quyền Khách hàng cho nhân viên!");
                }

                Integer finalRestaurantId = restaurantId;

                if (java.util.Objects.equals(roleId, 2)) {
                    finalRestaurantId = null;
                }
                else if (!hasRole(authentication)) {
                    finalRestaurantId = (Integer) session.getAttribute("currentBranchId");
                }

                if (user.getUserId() == null) {
                    userService.createNewUserFromAdmin(user, password, roleId, finalRestaurantId);
                } else {
                    userService.updateUserAdmin(user.getUserId(), user.getFullName(),
                            user.getEmail(), user.getPhone(),
                            roleId, finalRestaurantId);
                }
                return "redirect:/admin/staff?success";

            } else {
                if (user.getUserId() == null) {
                    userService.createNewUserFromAdmin(user, password, 1, null);
                } else {
                    userService.updateUserAdmin(user.getUserId(), user.getFullName(),
                            user.getEmail(), user.getPhone(),
                            1, null);
                }
                return "redirect:/admin/customer?success";
            }
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("customer", user);
            model.addAttribute("isStaffPage", isStaffPage);
            model.addAttribute("title", user.getUserId() == null ? "Thêm mới" : "Chỉnh sửa");
            model.addAttribute("backUrl", Boolean.TRUE.equals(isStaffPage) ? "/admin/staff" : "/admin/customer");

            prepareFormModel(model, authentication, session, roleId, restaurantId);
            return "admin/user-form";
        }
    }

    private void prepareFormModel(Model model, Authentication auth, HttpSession session, Integer currentRoleId, Integer currentResId) {
        Boolean isStaffPage = (Boolean) model.getAttribute("isStaffPage");

        if (Boolean.TRUE.equals(isStaffPage)) {
            List<Role> roles = roleService.getAllRoles();
            if (!hasRole(auth)) {
                roles = roles.stream()
                        .filter(r -> !r.getName().equals("ROLE_ADMIN"))
                        .collect(Collectors.toList());
            }
            model.addAttribute("allRoles", roles);
            model.addAttribute("allRestaurants", restaurantService.getAllActiveBranches());

            if (!hasRole(auth)) {
                model.addAttribute("currentRestaurantId", session.getAttribute("currentBranchId"));
            } else {
                model.addAttribute("currentRestaurantId", currentResId);
            }
        }
    }

    private boolean hasRole(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}