package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import restaurant.entity.Voucher;

import java.util.List;

public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    @Query("SELECT v FROM Voucher v " +
            "LEFT JOIN v.applicableRestaurants vr " +
            "WHERE v.applyType = 'ALL' OR vr.restaurant.restaurantId = :branchId")
    List<Voucher> findVouchersByBranch(@Param("branchId") Integer branchId);
}