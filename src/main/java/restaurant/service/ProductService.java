package restaurant.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurant.dto.ProductBranchDTO;
import restaurant.entity.*;
import restaurant.repository.DiscountRepository;
import restaurant.repository.ProductRepository;
import restaurant.repository.RestaurantProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final RestaurantProductRepository restaurantProductRepository;
    private final DiscountRepository discountRepository;

    public ProductBranchDTO getProductDtoForBranch(Integer branchId, Long productId) {
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn"));

        RestaurantProduct rp = restaurantProductRepository
                .findByRestaurant_RestaurantIdAndProduct_ProductId(branchId, productId)
                .orElse(new RestaurantProduct());

        ProductBranchDTO dto = new ProductBranchDTO();
        dto.setProductId(p.getProductId());
        dto.setName(p.getName());
        dto.setImageUrl(p.getImageUrl());
        dto.setDescription(p.getDescription());

        dto.setType(p.getType());
        dto.setUnit(p.getUnit());
        dto.setPreparationTime(p.getPreparationTime());

        if (p.getCategory() != null) {
            dto.setCategoryId(p.getCategory().getCategoryId());
            dto.setCategoryName(p.getCategory().getCategoryName());
        }

        dto.setPrice(rp.getPrice());
        dto.setStockQuantity(rp.getStockQuantity());
        dto.setIsAvailable(rp.getIsAvailable() != null ? rp.getIsAvailable() : true);
        dto.setIsFeatured(rp.getIsFeatured() != null ? rp.getIsFeatured() : false);

        return dto;
    }

    @Transactional
    public void saveProductFromDto(ProductBranchDTO dto, Integer branchId) {
        if (branchId == null) {
            throw new RuntimeException("Lỗi hệ thống: Không tìm thấy chi nhánh!");
        }

        Product p = (dto.getProductId() != null)
                ? productRepository.findById(dto.getProductId()).orElse(new Product())
                : new Product();

        p.setName(dto.getName());
        p.setDescription(dto.getDescription());

        p.setType(dto.getType());
        p.setUnit(dto.getUnit());
        p.setPreparationTime(dto.getPreparationTime());

        if (dto.getImageUrl() != null) {
            p.setImageUrl(dto.getImageUrl());
        }

        if (dto.getCategoryId() != null) {
            Category cat = new Category();
            cat.setCategoryId(dto.getCategoryId());
            p.setCategory(cat);
        }
        Product savedProduct = productRepository.save(p);

        RestaurantProduct rp = restaurantProductRepository
                .findByRestaurant_RestaurantIdAndProduct_ProductId(branchId, savedProduct.getProductId())
                .orElse(new RestaurantProduct());

        Restaurant res = new Restaurant();
        res.setRestaurantId(branchId);

        rp.setRestaurant(res);
        rp.setProduct(savedProduct);

        rp.setPrice(dto.getPrice());
        rp.setStockQuantity(dto.getStockQuantity() != null ? dto.getStockQuantity() : 0);
        rp.setIsAvailable(dto.getIsAvailable() != null ? dto.getIsAvailable() : true);
        rp.setIsFeatured(dto.getIsFeatured() != null ? dto.getIsFeatured() : false);

        restaurantProductRepository.saveAndFlush(rp);
    }

    @Transactional
    public void removeProductFromBranch(Integer branchId, Long productId) {
        productRepository.findById(productId).ifPresent(p -> {

            restaurantProductRepository.findByRestaurant_RestaurantIdAndProduct_ProductId(branchId, productId)
                    .ifPresent(restaurantProductRepository::delete);

            productRepository.delete(p);
        });
    }

    public Page<RestaurantProduct> searchAndFilter(Integer branchId, String keyword, Integer parentId, Integer categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("restaurantProductId").descending());

        Page<RestaurantProduct> pageResult = restaurantProductRepository.findByBranchAndFilters(branchId, keyword, parentId, categoryId, pageable);

        pageResult.forEach(this::calculateActiveDiscount);

        return pageResult;
    }

    private void calculateActiveDiscount(RestaurantProduct rp) {
        if (rp.getProductDiscounts() != null && !rp.getProductDiscounts().isEmpty()) {
            LocalDateTime now = LocalDateTime.now();

            rp.getProductDiscounts().stream()
                    .filter(pd -> pd.getDiscount() != null && pd.getDiscount().getStatus() == 1)
                    .filter(pd -> now.isAfter(pd.getDiscount().getStartDate()) && now.isBefore(pd.getDiscount().getEndDate()))
                    .findFirst()
                    .ifPresent(pd -> {
                        rp.setDiscountPrice(pd.getSpecialPrice());
                    });
        }
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn"));
    }

    public List<RestaurantProduct> getAllProductsByBranch(Integer branchId) {
        return restaurantProductRepository.findByRestaurant_RestaurantId(branchId);
    }

    public Map<String, Long> getMarketStats(Integer branchId) {
        int marketParentId = 2;

        long totalItems = restaurantProductRepository.countByBranchAndParentCategory(branchId, marketParentId);
        long inStock = restaurantProductRepository.countInStock(branchId, marketParentId);
        long outOfStock = totalItems - inStock;
        Long totalInv = restaurantProductRepository.sumStockQuantity(branchId, marketParentId);

        Map<String, Long> stats = new HashMap<>();
        stats.put("totalItems", totalItems);
        stats.put("inStock", inStock);
        stats.put("outOfStock", outOfStock);
        stats.put("totalInventory", totalInv != null ? totalInv : 0L);

        return stats;
    }
}