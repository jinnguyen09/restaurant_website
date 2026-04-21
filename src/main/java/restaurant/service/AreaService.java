package restaurant.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurant.entity.Area;
import restaurant.repository.AreaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;

    public List<Area> getAllByRestaurant(Integer restaurantId) {
        return areaRepository.findByRestaurantId(restaurantId);
    }

    @Transactional
    public Area saveArea(Area area, Integer restaurantId) {
        area.setRestaurantId(restaurantId);
        return areaRepository.save(area);
    }

    @Transactional
    public void deleteArea(Integer areaId, Integer restaurantId) {
        Area area = areaRepository.findByAreaIdAndRestaurantId(areaId, restaurantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khu vực hoặc bạn không có quyền"));
        areaRepository.delete(area);
    }
}