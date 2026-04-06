package restaurant.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurant.entity.Restaurant;
import restaurant.repository.RestaurantRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository repo;

    public List<Restaurant> getAllActiveBranches() {
        return repo.findByStatus(1);
    }

    public Restaurant getBranchById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    public String getBranchNameById(Integer id) {
        Restaurant res = getBranchById(id);
        return (res != null) ? res.getName() : "Chọn chi nhánh";
    }

    public Restaurant getRestaurantInfo(Integer branchId) {
        return repo.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin nhà hàng"));
    }

    public List<Restaurant> getAllBranches() {
        return repo.findAll();
    }

    @Transactional
    public void saveBranch(Restaurant restaurant) {
        if (restaurant.getRestaurantId() != null) {
            Restaurant existing = repo.findById(restaurant.getRestaurantId())
                    .orElseThrow(() -> new RuntimeException("Chi nhánh không tồn tại"));

            restaurant.setCreatedAt(existing.getCreatedAt());
        }

        repo.save(restaurant);
    }

    @Transactional
    public void toggleStatus(Integer id) {
        Restaurant res = getRestaurantInfo(id);
        res.setStatus(res.getStatus() == 1 ? 0 : 1);
        repo.save(res);
    }
}