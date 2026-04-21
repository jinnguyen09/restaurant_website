package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import restaurant.entity.Voucher;
import restaurant.entity.VoucherRestaurant;

@Repository
public interface VoucherRestaurantRepository extends JpaRepository<VoucherRestaurant, Integer> {

    boolean existsByVoucherVoucherIdAndRestaurantRestaurantId(Integer voucherId, Integer restaurantId);

    void deleteByVoucher(Voucher voucher);
}