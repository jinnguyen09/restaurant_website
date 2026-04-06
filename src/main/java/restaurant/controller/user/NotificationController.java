package restaurant.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import restaurant.entity.Notification;
import restaurant.entity.User;
import restaurant.service.NotificationService;
import restaurant.service.UserService;
import org.springframework.data.domain.Page;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String showNotifications(Model model, Principal principal,
                                    @RequestParam(defaultValue = "0") int page) {
        User user = userService.findByEmail(principal.getName());

        Page<Notification> notificationPage = notificationService.getNotificationsForUser(user, page);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", notificationPage.getTotalPages());
        model.addAttribute("notifications", notificationService.getNotificationsForUser(user));
        model.addAttribute("unreadCount", notificationService.countUnread(user));

        return "user/notification";
    }

    @PostMapping("/mark-all-read")
    public String markAllRead(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        notificationService.markAllAsRead(user);
        return "redirect:/notification";
    }
}
