package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import restaurant.entity.Area;
import java.util.List;
import java.util.Optional;

@Repository
public interface AreaRepository extends JpaRepository<Area, Integer> {

    List<Area> findByRestaurantId(Integer restaurantId);

    Optional<Area> findByAreaIdAndRestaurantId(Integer areaId, Integer restaurantId);

    void deleteByAreaIdAndRestaurantId(Integer areaId, Integer restaurantId);
}