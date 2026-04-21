package restaurant.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurant.entity.Notification;
import restaurant.entity.User;
import restaurant.entity.UserVoucher;
import restaurant.entity.Voucher;
import restaurant.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final UserVoucherRepository userVoucherRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final VoucherRestaurantRepository voucherRestaurantRepository;
    private final RestaurantRepository restaurantRepository;

    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    public Voucher getVoucherById(Integer id) {
        return voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá với ID: " + id));
    }

    @Transactional
    public void saveVoucher(Voucher voucher) {
        if (voucher.getMinOrderValue() == null) {
            voucher.setMinOrderValue(java.math.BigDecimal.ZERO);
        }
        voucherRepository.save(voucher);
    }

    @Transactional
    public void deleteVoucher(Integer id) {
        Voucher voucher = getVoucherById(id);
        voucherRestaurantRepository.deleteByVoucher(voucher);
        voucherRepository.delete(voucher);
    }

    public boolean isValidVoucher(Voucher voucher, User user, java.math.BigDecimal orderValue, Integer restaurantId) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(voucher.getExpiryDate())) return false;

        if (orderValue.compareTo(voucher.getMinOrderValue()) < 0) return false;

        if (voucher.getUsageLimit() != null && voucher.getUsageLimit() <= 0) return false;

        if (voucher.getApplyType() == Voucher.ApplyType.SPECIFIC) {
            boolean isBranchValid = voucherRestaurantRepository.existsByVoucherVoucherIdAndRestaurantRestaurantId(
                    voucher.getVoucherId(), restaurantId);
            if (!isBranchValid) return false;
        }
        return userVoucherRepository.existsByUserAndVoucherAndUsedAtIsNull(user, voucher);
    }

    @Transactional
    public void useVoucher(User user, Voucher voucher) {
        if (voucher.getUsageLimit() != null && voucher.getUsageLimit() > 0) {
            voucher.setUsageLimit(voucher.getUsageLimit() - 1);
            voucherRepository.save(voucher);
        }

        userVoucherRepository.findFirstByUserAndVoucherAndUsedAtIsNull(user, voucher)
                .ifPresent(uv -> {
                    uv.setUsedAt(LocalDateTime.now());
                    userVoucherRepository.save(uv);
                });
    }

    @Transactional
    public void giveVoucherToUser(Integer voucherId, String identifier) {
        Voucher voucher = getVoucherById(voucherId);
        User user = userRepository.findByEmailOrPhone(identifier)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng: " + identifier));
        saveUserVoucher(user, voucher);
    }

    @Async
    @Transactional
    public void giveVoucherByMembershipAsync(Integer voucherId, String rankName) {
        Voucher voucher = getVoucherById(voucherId);
        List<User> users = userRepository.findByRankName(rankName);
        processMassVoucherGiving(users, voucher);
    }

    @Async
    @Transactional
    public void giveVoucherToAllAsync(Integer voucherId) {
        Voucher voucher = getVoucherById(voucherId);
        List<User> allUsers = userRepository.findAll();
        processMassVoucherGiving(allUsers, voucher);
    }

    private void saveUserVoucher(User user, Voucher voucher) {
        boolean isHoldingUnused = userVoucherRepository.existsByUserAndVoucherAndUsedAtIsNull(user, voucher);

        if (!isHoldingUnused) {
            UserVoucher uv = new UserVoucher();
            uv.setUser(user);
            uv.setVoucher(voucher);
            uv.setAssignedAt(LocalDateTime.now());
            userVoucherRepository.save(uv);

            sendVoucherNotification(user,
                    "🎁 Quà tặng Voucher mới!",
                    "Chúc mừng! Bạn vừa nhận được mã '" + voucher.getVoucherName() + "' giảm " + String.format("%,.0f", voucher.getDiscountAmount()) + "đ.");
        } else {
            sendVoucherNotification(user,
                    "⏰ Đừng quên ưu đãi của bạn!",
                    "Bạn vẫn còn mã '" + voucher.getVoucherName() + "' chưa sử dụng trong kho quà đấy!");
        }
    }

    private void processMassVoucherGiving(List<User> users, Voucher voucher) {
        List<UserVoucher> listToSave = new ArrayList<>();
        List<Notification> listNoti = new ArrayList<>();

        for (User user : users) {
            if (!userVoucherRepository.existsByUserAndVoucherAndUsedAtIsNull(user, voucher)) {
                UserVoucher uv = new UserVoucher();
                uv.setUser(user);
                uv.setVoucher(voucher);
                uv.setAssignedAt(LocalDateTime.now());
                listToSave.add(uv);

                Notification noti = new Notification();
                noti.setUser(user);
                noti.setTitle("🎁 Ưu đãi đặc biệt!");
                noti.setContent("Bạn nhận được mã '" + voucher.getVoucherName() + "' trị giá " + String.format("%,.0f", voucher.getDiscountAmount()) + "đ.");
                noti.setRead(false);
                noti.setStatus("active");
                noti.setLinkUrl("/my-vouchers");
                noti.setCreateAt(LocalDateTime.now());
                listNoti.add(noti);
            }
        }
        if (!listToSave.isEmpty()) userVoucherRepository.saveAll(listToSave);
        if (!listNoti.isEmpty()) notificationRepository.saveAll(listNoti);
    }

    private void sendVoucherNotification(User user, String title, String content) {
        Notification noti = new Notification();
        noti.setUser(user);
        noti.setTitle(title);
        noti.setContent(content);
        noti.setRead(false);
        noti.setStatus("active");
        noti.setLinkUrl("/my-vouchers");
        noti.setCreateAt(LocalDateTime.now());
        notificationRepository.save(noti);
    }

    public List<UserVoucher> getVouchersByUser(User user) {
        return userVoucherRepository.findByUser(user);
    }

    public List<UserVoucher> getAvailableVouchersForUser(User user, Integer restaurantId) {
        List<UserVoucher> allMyVouchers = userVoucherRepository.findByUserAndUsedAtIsNull(user);

        return allMyVouchers.stream()
                .filter(uv -> {
                    Voucher v = uv.getVoucher();
                    if (v.getApplyType() == Voucher.ApplyType.ALL) return true;

                    return voucherRestaurantRepository.existsByVoucherVoucherIdAndRestaurantRestaurantId(
                            v.getVoucherId(), restaurantId);
                })
                .toList();
    }

    @Transactional
    public void saveVoucherWithRestaurants(Voucher voucher, List<Integer> restaurantIds) {
        if (voucher.getMinOrderValue() == null) {
            voucher.setMinOrderValue(java.math.BigDecimal.ZERO);
        }
        if (voucher.getVoucherId() == null) {
            Voucher savedVoucher = voucherRepository.save(voucher);

            if (voucher.getApplyType() == Voucher.ApplyType.SPECIFIC && restaurantIds != null) {
                for (Integer resId : restaurantIds) {
                    restaurantRepository.findById(resId).ifPresent(res -> {
                        restaurant.entity.VoucherRestaurant vr = new restaurant.entity.VoucherRestaurant();
                        vr.setVoucher(savedVoucher);
                        vr.setRestaurant(res);
                        voucherRestaurantRepository.save(vr);
                    });
                }
            }
        }
        else {
            voucherRepository.save(voucher);
        }
    }
    public List<Voucher> getVouchersByBranch(Integer branchId) {
        return voucherRepository.findVouchersByBranch(branchId);
    }
}