package restaurant.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.entity.Discount;
import restaurant.entity.ProductDiscount;
import restaurant.entity.RestaurantProduct;
import restaurant.repository.DiscountRepository;
import restaurant.repository.ProductDiscountRepository;
import restaurant.repository.RestaurantProductRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final ProductDiscountRepository productDiscountRepository;
    private final RestaurantProductRepository restaurantProductRepository;

    public List<Discount> getDiscountsByBranch(Integer branchId) {
        return discountRepository.findByRestaurant_RestaurantIdOrderByDiscountIdDesc(branchId);
    }

    @Transactional
    public void saveDiscount(Discount discount, Integer branchId) {
        if (discount.getStatus() == null) discount.setStatus(1);

        restaurant.entity.Restaurant res = new restaurant.entity.Restaurant();
        res.setRestaurantId(branchId);
        discount.setRestaurant(res);

        discountRepository.save(discount);
    }

    @Transactional
    public void updateDiscountApplications(Long discountId, List<Long> restaurantProductIds, Integer branchId) {
        getDiscountById(discountId);

        productDiscountRepository.deleteByDiscountAndBranch(discountId, branchId);

        if (restaurantProductIds != null && !restaurantProductIds.isEmpty()) {
            for (Long rpId : restaurantProductIds) {
                applyDiscount(discountId, rpId);
            }
        }
    }

    @Transactional
    public void applyDiscount(Long discountId, Long restaurantProductId) {
        Discount discount = getDiscountById(discountId);
        RestaurantProduct rp = restaurantProductRepository.findById(restaurantProductId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn tại chi nhánh"));

        BigDecimal originalPrice = rp.getPrice();
        BigDecimal value = discount.getDiscountValue();
        BigDecimal specialPrice;

        if ("PERCENTAGE".equals(discount.getDiscountType())) {
            BigDecimal discountRate = value.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            specialPrice = originalPrice.multiply(BigDecimal.ONE.subtract(discountRate));
        } else {
            specialPrice = originalPrice.subtract(value);
        }

        specialPrice = specialPrice.setScale(0, RoundingMode.HALF_UP);
        if (specialPrice.compareTo(BigDecimal.ZERO) < 0) specialPrice = BigDecimal.ZERO;

        productDiscountRepository.deleteByRestaurantProductId(restaurantProductId);

        ProductDiscount pd = ProductDiscount.builder()
                .discount(discount)
                .restaurantProduct(rp)
                .specialPrice(specialPrice)
                .build();

        productDiscountRepository.save(pd);
    }

    public Discount getDiscountById(Long id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương trình này"));
    }

    @Transactional
    public void deleteDiscount(Long id) {
        Discount discount = getDiscountById(id);
        discountRepository.delete(discount);
    }
}