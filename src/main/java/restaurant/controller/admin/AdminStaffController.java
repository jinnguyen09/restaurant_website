package restaurant.controller.admin;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import restaurant.entity.Role;
import restaurant.entity.User;
import restaurant.service.RestaurantService;
import restaurant.service.RoleService;
import restaurant.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/staff")
public class AdminStaffController {

    @Autowired private UserService userService;
    @Autowired private RoleService roleService;
    @Autowired private RestaurantService restaurantService;

    @GetMapping
    public String listStaffs(Model model,
                             @RequestParam(name = "search", required = false) String search,
                             HttpSession session) {
        Integer branchId = (Integer) session.getAttribute("currentBranchId");

        if (branchId != null) {
            String branchName = restaurantService.getBranchNameById(branchId);
            model.addAttribute("currentBranchName", branchName);
            session.setAttribute("currentBranchName", branchName);
        }

        model.addAttribute("staffs", userService.getStaffStats(search, branchId));
        model.addAttribute("keyword", search);
        model.addAttribute("activePage", "staff");
        return "admin/admin-staff";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public String showAddStaffForm(Model model, Authentication authentication, HttpSession session) {
        model.addAttribute("customer", new User());
        model.addAttribute("isStaffPage", true);
        model.addAttribute("backUrl", "/admin/staff");
        model.addAttribute("title", "Thêm nhân viên mới");

        prepareFormModel(model, authentication, session, null, null);
        return "admin/user-form";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public String showEditStaffForm(@PathVariable Long id, Model model, Authentication authentication, HttpSession session) {
        User user = userService.getById(id);
        Integer targetUserRoleId = userService.getCurrentRoleId(id);

        if (targetUserRoleId != null && targetUserRoleId == 2 && !hasRole(authentication)) {
            return "redirect:/admin/staff?error=unauthorized";
        }

        model.addAttribute("customer", user);
        model.addAttribute("title", "Chỉnh sửa nhân viên: " + user.getFullName());
        model.addAttribute("isStaffPage", true);
        model.addAttribute("backUrl", "/admin/staff");

        prepareFormModel(model, authentication, session, targetUserRoleId, userService.getCurrentRestaurantId(id));
        return "admin/user-form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public String saveStaff(@ModelAttribute("customer") User user,
                            @RequestParam(value = "password", required = false) String password,
                            @RequestParam(value = "roleId", required = false) Integer roleId,
                            @RequestParam(value = "restaurantId", required = false) Integer restaurantId,
                            Authentication authentication, HttpSession session, Model model) {
        try {
            User loggedInUser = userService.getByEmail(authentication.getName());

            if (user.getUserId() != null) {
                if (user.getUserId().equals(loggedInUser.getUserId())) {
                    Integer oldRole = userService.getCurrentRoleId(user.getUserId());
                    Integer oldRes = userService.getCurrentRestaurantId(user.getUserId());
                    if (!roleId.equals(oldRole) || (restaurantId != null && !restaurantId.equals(oldRes))) {
                        throw new RuntimeException("Bạn không thể tự thay đổi quyền hạn hoặc chi nhánh của chính mình!");
                    }
                }

                Integer targetRole = userService.getCurrentRoleId(user.getUserId());
                if (targetRole == 2 && !hasRole(authentication)) {
                    throw new RuntimeException("Bạn không có quyền chỉnh sửa tài khoản Quản trị viên!");
                }
            }

            if (!hasRole(authentication) && roleId == 2) throw new RuntimeException("Không được phép gán quyền Admin!");
            if (roleId == 1) throw new RuntimeException("Không được gán quyền Khách hàng tại đây!");

            Integer finalResId = hasRole(authentication) ? restaurantId : (Integer) session.getAttribute("currentBranchId");

            if (user.getUserId() == null) {
                userService.createNewUserFromAdmin(user, password, roleId, finalResId);
            } else {
                userService.updateUserAdmin(user.getUserId(), user.getFullName(), user.getEmail(), user.getPhone(), roleId, finalResId);
            }
            return "redirect:/admin/staff?success";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("customer", user);
            model.addAttribute("isStaffPage", true);
            model.addAttribute("title", user.getUserId() == null ? "Thêm mới" : "Chỉnh sửa");
            prepareFormModel(model, authentication, session, roleId, restaurantId);
            return "admin/user-form";
        }
    }

    private void prepareFormModel(Model model, Authentication auth, HttpSession session, Integer currentRoleId, Integer currentResId) {
        User loggedInUser = userService.getByEmail(auth.getName());
        model.addAttribute("currentLoggedInId", loggedInUser != null ? loggedInUser.getUserId() : null);
        boolean isAdmin = hasRole(auth);
        List<Role> roles = roleService.getAllRoles().stream()
                .filter(r -> !r.getName().equals("ROLE_USER"))
                .filter(r -> isAdmin || !r.getName().equals("ROLE_ADMIN"))
                .collect(Collectors.toList());

        if (!isAdmin) {
            String sessionBranchName = (String) session.getAttribute("currentBranchName");
            model.addAttribute("currentBranchName", sessionBranchName);
            model.addAttribute("currentRestaurantId", session.getAttribute("currentBranchId"));
        } else {
            model.addAttribute("currentRestaurantId", currentResId);
            if (currentResId != null) {
                model.addAttribute("currentBranchName", restaurantService.getBranchNameById(currentResId));
            }
        }

        model.addAttribute("allRoles", roles);
        model.addAttribute("currentRoleId", currentRoleId);
        model.addAttribute("allRestaurants", restaurantService.getAllActiveBranches());

        if (!isAdmin) {
            model.addAttribute("currentRestaurantId", session.getAttribute("currentBranchId"));
            model.addAttribute("currentBranchName", session.getAttribute("currentBranchName"));
        } else {
            model.addAttribute("currentRestaurantId", currentResId);
        }
    }

    private boolean hasRole(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}