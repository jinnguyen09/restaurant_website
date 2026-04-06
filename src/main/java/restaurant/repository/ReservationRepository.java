package restaurant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restaurant.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUser_UserId(Long userId);

    List<Reservation> findByRestaurant_RestaurantId(Integer restaurantId);

    List<Reservation> findByStatus(String status);

    List<Reservation> findByReservationTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.user WHERE r.restaurant.restaurantId = :resId")
    List<Reservation> findAllByRestaurantWithUser(@Param("resId") Integer resId);

    boolean existsByUser_UserIdAndReservationTime(Long userId, LocalDateTime time);

    Page<Reservation> findByRestaurant_RestaurantId(Integer branchId, Pageable pageable);
}