package restaurant.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import restaurant.entity.RestaurantProduct;
import restaurant.repository.RestaurantProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantProductService {

    private final RestaurantProductRepository restaurantProductRepository;

    public List<RestaurantProduct> findByRestaurantId(Integer branchId) {
        return restaurantProductRepository.findAllWithProductInfo(branchId);
    }

    public Page<RestaurantProduct> searchAndFilter(Integer branchId, String keyword, Integer parentId, Integer categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("restaurantProductId").descending());
        return restaurantProductRepository.findByBranchAndFilters(branchId, keyword, parentId, categoryId, pageable);
    }
}