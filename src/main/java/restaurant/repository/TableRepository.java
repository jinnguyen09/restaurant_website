package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import restaurant.entity.TableEntity;
import restaurant.enums.TableStatus;
import java.util.List;
import java.util.Optional;

@Repository
public interface TableRepository extends JpaRepository<TableEntity, Long> {

    List<TableEntity> findByRestaurantId(Integer restaurantId);

    List<TableEntity> findByArea_AreaIdAndRestaurantId(Integer areaId, Integer restaurantId);

    List<TableEntity> findByRestaurantIdAndStatus(Integer restaurantId, TableStatus status);

    Optional<TableEntity> findByTableIdAndRestaurantId(Long tableId, Integer restaurantId);

    boolean existsByTableNumberAndRestaurantId(String tableNumber, Integer restaurantId);
}