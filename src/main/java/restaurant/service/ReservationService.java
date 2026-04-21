package restaurant.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurant.entity.Reservation;
import restaurant.entity.Restaurant;
import restaurant.repository.ReservationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public Page<Reservation> searchReservations(Integer branchId, String filter, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("reservationTime").descending());

        if (keyword != null && !keyword.trim().isEmpty()) {
            return reservationRepository.searchByKeyword(branchId, keyword, pageable);
        }

        if ("today".equals(filter)) {
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
            return reservationRepository.findByRestaurant_RestaurantIdAndReservationTimeBetween(branchId, startOfDay, endOfDay, pageable);
        } else if ("upcoming".equals(filter)) {
            return reservationRepository.findByRestaurant_RestaurantIdAndReservationTimeAfter(branchId, LocalDateTime.now(), pageable);
        }

        return reservationRepository.findByRestaurant_RestaurantId(branchId, pageable);
    }


    public Reservation getReservationForBranch(Integer branchId, Long reservationId) {
        return reservationRepository.findById(reservationId)
                .filter(res -> res.getRestaurant().getRestaurantId().equals(branchId))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt bàn tại chi nhánh này!"));
    }

    @Transactional
    public void saveReservationFromBranch(Reservation data, Integer branchId) {
        if (branchId == null) {
            throw new RuntimeException("Lỗi hệ thống: Không xác định được chi nhánh!");
        }

        Reservation res;
        if (data.getReservationId() != null) {
            res = getReservationForBranch(branchId, data.getReservationId());
        } else {
            res = new Reservation();
            Restaurant restaurant = new Restaurant();
            restaurant.setRestaurantId(branchId);
            res.setRestaurant(restaurant);
        }

        res.setUser(data.getUser());
        res.setReservationTime(data.getReservationTime());
        res.setNumberOfPeople(data.getNumberOfPeople());
        res.setStatus(data.getStatus() != null ? data.getStatus() : "PENDING");
        res.setDescription(data.getDescription());

        reservationRepository.save(res);
    }

    @Transactional
    public void removeReservationFromBranch(Integer branchId, Long reservationId) {
        Reservation res = getReservationForBranch(branchId, reservationId);
        reservationRepository.delete(res);
    }

    @Transactional
    public void updateStatus(Integer branchId, Long reservationId, String status) {
        Reservation res = getReservationForBranch(branchId, reservationId);
        res.setStatus(status);
        reservationRepository.save(res);
    }
}