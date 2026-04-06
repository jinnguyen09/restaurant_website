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

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public Page<Reservation> getReservationsByBranch(Integer branchId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("reservationTime").descending());
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