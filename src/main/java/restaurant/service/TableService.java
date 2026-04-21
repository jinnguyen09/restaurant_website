package restaurant.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurant.entity.Area;
import restaurant.entity.TableEntity;
import restaurant.enums.TableStatus;
import restaurant.repository.AreaRepository;
import restaurant.repository.TableRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;
    private final AreaRepository areaRepository;

    public List<TableEntity> getTablesByBranch(Integer restaurantId) {
        return tableRepository.findByRestaurantId(restaurantId);
    }

    @Transactional
    public TableEntity createTable(Integer areaId, TableEntity table, Integer restaurantId) {
        Area area = areaRepository.findByAreaIdAndRestaurantId(areaId, restaurantId)
                .orElseThrow(() -> new RuntimeException("Khu vực không hợp lệ cho chi nhánh này"));

        if (tableRepository.existsByTableNumberAndRestaurantId(table.getTableNumber(), restaurantId)) {
            throw new RuntimeException("Số bàn này đã tồn tại trong hệ thống chi nhánh");
        }

        table.setArea(area);
        table.setRestaurantId(restaurantId);
        table.setStatus(TableStatus.AVAILABLE);
        return tableRepository.save(table);
    }

    @Transactional
    public TableEntity updateStatus(Long tableId, TableStatus newStatus, Integer restaurantId) {
        TableEntity table = tableRepository.findByTableIdAndRestaurantId(tableId, restaurantId)
                .orElseThrow(() -> new RuntimeException("Bàn không tồn tại"));

        table.setStatus(newStatus);
        return tableRepository.save(table);
    }

    @Transactional
    public void deleteTable(Long tableId, Integer restaurantId) {
        TableEntity table = tableRepository.findByTableIdAndRestaurantId(tableId, restaurantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn để xóa"));
        tableRepository.delete(table);
    }

    public List<TableEntity> getTablesByArea(Integer areaId, Integer restaurantId) {
        return tableRepository.findByArea_AreaIdAndRestaurantId(areaId, restaurantId);
    }
}