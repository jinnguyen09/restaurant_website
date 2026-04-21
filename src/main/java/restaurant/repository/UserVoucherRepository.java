package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import restaurant.entity.User;
import restaurant.entity.UserVoucher;
import restaurant.entity.Voucher;

import java.util.List;
import java.util.Optional;

public interface UserVoucherRepository extends JpaRepository<UserVoucher, Integer> {

    boolean existsByUserAndVoucher(User user, Voucher voucher);

    boolean existsByUserAndVoucherAndUsedAtIsNull(User user, Voucher voucher);

    List<UserVoucher> findByUser(User user);

    List<UserVoucher> findByUserAndUsedAtIsNull(User user);

    Optional<UserVoucher> findFirstByUserAndVoucherAndUsedAtIsNull(User user, Voucher voucher);
}