package restaurant.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import restaurant.config.CustomUserDetails;
import restaurant.dto.CustomerDTO;
import restaurant.dto.UserUpdateDTO;
import restaurant.entity.*;
import restaurant.repository.*;

import java.io.IOException;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired private RankRepository rankRepository;
    @Autowired private PointRepository pointRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private NotificationService notificationService;
    @Autowired private FileService fileService;
    @Autowired private RoleRepository roleRepository;
    @Autowired private UserRoleRepository userRoleRepository;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));
    }

    @Transactional
    public void registerNewUser(User user, Role defaultRole) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setPoints(50);

        User savedUser = userRepository.save(user);

        UserRole userRole = new UserRole();
        userRole.setUser(savedUser);
        userRole.setRole(defaultRole);
        savedUser.getUserRoles().add(userRole);

        Point bonusPoint = new Point();
        bonusPoint.setUser(savedUser);
        bonusPoint.setAmount(50);
        bonusPoint.setType("earn");
        bonusPoint.setSourceType("register");
        pointRepository.save(bonusPoint);

        Notification welcomeNoti = new Notification();
        welcomeNoti.setUser(savedUser);
        welcomeNoti.setTitle("Chào mừng bạn!");
        welcomeNoti.setContent("Tài khoản của bạn đã được khởi tạo thành công với 50 điểm thưởng.");
        welcomeNoti.setStatus("active");
        notificationRepository.save(welcomeNoti);
    }

    @Transactional
    public User updateUser(String email, UserUpdateDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + email));

        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());

        userRepository.save(user);
        return user;
    }

    public void updateSecurityContext(User user) {
        CustomUserDetails newUserDetails = new CustomUserDetails(user);

        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                newUserDetails,
                null,
                newUserDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    @Transactional
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không chính xác!");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new RuntimeException("Mật khẩu mới không được trùng với mật khẩu cũ!");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        updateSecurityContext(user);
    }

    @Transactional
    public String updateAvatar(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        String oldAvatarUrl = user.getAvatar();

        String newAvatarPath = fileService.saveAvatar(file);

        if (newAvatarPath != null) {
            user.setAvatar(newAvatarPath);
            userRepository.save(user);

            updateSecurityContext(user);

            fileService.deleteOldFile(oldAvatarUrl);

            return newAvatarPath;
        }
        return oldAvatarUrl;
    }

    @Transactional
    public boolean addPoints(Long userId, int pointsToAdd) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Ranked oldRank = user.getRank();
        int newPoints = user.getPoints() + pointsToAdd;
        user.setPoints(newPoints);

        Ranked newRank = rankRepository.findFirstByMinPointsLessThanEqualOrderByMinPointsDesc(newPoints)
                .orElse(oldRank);

        boolean isRankUp = false;

        if (newRank != null) {
            if (oldRank == null || !newRank.getRankId().equals(oldRank.getRankId())) {
                user.setRank(newRank);
                isRankUp = true;

                String title = "Chúc mừng nâng cấp hạng!";
                String content = "Bạn đã đạt mức điểm " + newPoints + " và được nâng lên hạng " + newRank.getRankName() + ".";
                String status = "UNREAD";
                String url = "/profile";
                notificationService.createNotification(user, title, content, status, url);
            }
        }

        userRepository.save(user);
        return isRankUp;
    }

    public List<CustomerDTO> getCustomerStats(String keyword, Integer branchId) {
        return userRepository.findAllCustomerStats(keyword);
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
    }

    public Integer getCurrentRoleId(Long userId) {
        List<UserRole> roles = userRoleRepository.findAllByUser_UserId(userId);
        return roles.isEmpty() ? null : roles.getFirst().getRole().getRoleId();
    }

    public Integer getCurrentRestaurantId(Long userId) {
        List<UserRole> roles = userRoleRepository.findAllByUser_UserId(userId);

        if (roles.isEmpty() || roles.getFirst().getRestaurant() == null) {
            return null;
        }
        return roles.getFirst().getRestaurant().getRestaurantId();
    }

    @Transactional
    public void updateUserAdmin(Long userId, String fullName, String email, String phone, Integer roleId, Integer restaurantId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        userRepository.findByEmail(email).ifPresent(u -> {
            if (!u.getUserId().equals(userId)) {
                throw new RuntimeException("Email này đã thuộc về người dùng khác!");
            }
        });

        User userByPhone = userRepository.findByEmailOrPhone(null, phone);
        if (userByPhone != null && !userByPhone.getUserId().equals(userId)) {
            throw new RuntimeException("Số điện thoại này đã được sử dụng!");
        }

        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        userRepository.save(user);

        UserRole userRole = userRoleRepository.findByUser_UserId(userId)
                .orElse(new UserRole());

        userRole.setUser(user);

        Role role = roleRepository.findById(roleId.longValue())
                .orElseThrow(() -> new RuntimeException("Quyền hạn không tồn tại"));
        userRole.setRole(role);

        userRole.setRestaurantId(restaurantId);

        userRoleRepository.save(userRole);
    }

    @Transactional
    public void createNewUserFromAdmin(User user, String rawPassword, Integer roleId, Integer restaurantId) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email này đã được sử dụng bởi một tài khoản khác!");
        }

        if (userRepository.existsByPhone(user.getPhone())) {
            throw new RuntimeException("Số điện thoại này đã được sử dụng!");
        }

        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setCreatedAt(java.time.LocalDateTime.now());
        user.setPoints(0);

        User savedUser = userRepository.save(user);

        UserRole userRole = new UserRole();
        userRole.setUser(savedUser);

        Role role = roleRepository.findById(roleId.longValue())
                .orElseThrow(() -> new RuntimeException("Role không tồn tại"));
        userRole.setRole(role);

        userRole.setRestaurantId(restaurantId);

        userRoleRepository.save(userRole);
    }

    public Object getAllCustomers() {
        return userRepository.findAll();
    }
}