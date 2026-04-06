package restaurant.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import restaurant.config.CustomUserDetails;
import restaurant.dto.UserUpdateDTO;
import restaurant.entity.User;
import restaurant.entity.Ranked;
import restaurant.repository.RankRepository;
import restaurant.service.FileService;
import restaurant.service.UserService;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    @Autowired
    private RankRepository rankRepository;

    @GetMapping
    public String profile(Model model, Authentication authentication, @RequestParam(name = "rankUp", required = false) Boolean rankUp) {
        if (authentication != null) {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            model.addAttribute("user", user);


            Optional<Ranked> nextRankOpt = rankRepository.findFirstByMinPointsGreaterThanOrderByMinPointsAsc(user.getPoints());

            String nextRankName = "MAX LEVEL";
            int nextRankPoints = user.getPoints();
            double progress = 100.0;

            if (nextRankOpt.isPresent()) {
                Ranked nextRank = nextRankOpt.get();
                nextRankName = nextRank.getRankName();
                nextRankPoints = nextRank.getMinPoints();

                if (nextRankPoints > 0) {
                    progress = (double) user.getPoints() / nextRankPoints * 100;
                }
            }

            model.addAttribute("nextRankName", nextRankName);
            model.addAttribute("nextRankPoints", nextRankPoints);
            model.addAttribute("progressPercentage", progress);
        }

        model.addAttribute("page", "profile");

        if (Boolean.TRUE.equals(rankUp)) {
            model.addAttribute("showRankUpAlert", true);
        }

        return "user/profile";
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateProfile(@RequestBody UserUpdateDTO dto, Authentication authentication) {
        try {
            String email = authentication.getName();
            User updatedUser = userService.updateUser(email, dto);
            userService.updateSecurityContext(updatedUser);
            return ResponseEntity.ok(Map.of("message", "Cập nhật thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    @ResponseBody
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> payload, Authentication auth) {
        try {
            String email = auth.getName();
            String oldPass = payload.get("oldPassword");
            String newPass = payload.get("newPassword");

            userService.changePassword(email, oldPass, newPass);

            return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/upload-avatar")
    @ResponseBody
    public ResponseEntity<?> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Phiên làm việc hết hạn");
            }

            String newUrl = userService.updateAvatar(userDetails.getUserId(), file);

            return ResponseEntity.ok(Map.of("url", newUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}