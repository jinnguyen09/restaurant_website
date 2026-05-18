package restaurant.service;

import restaurant.dto.CartItemDTO;
import restaurant.entity.CartItem;
import restaurant.entity.RestaurantProduct;
import restaurant.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurant.repository.RestaurantProductRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private RestaurantProductRepository restaurantProductRepository;

    public List<CartItemDTO> getCartItemsByUserId(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);

        return items.stream().map(item -> {
            RestaurantProduct rp = item.getRestaurantProduct();
            calculateActiveDiscount(rp);

            var product = rp.getProduct();

            double finalPrice = (rp.getDiscountPrice() != null)
                    ? rp.getDiscountPrice().doubleValue()
                    : rp.getPrice().doubleValue();

            return CartItemDTO.builder()
                    .cartItemId(item.getCartItemId())
                    .restaurantProductId(item.getRestaurantProductId())
                    .productId(product.getProductId())
                    .productName(product.getName())
                    .productImageUrl(product.getImageUrl())
                    .originalPrice(rp.getPrice().doubleValue())
                    .price(finalPrice)
                    .quantity(item.getQuantity())
                    .subTotal(finalPrice * item.getQuantity())
                    .restaurantId(rp.getRestaurant().getRestaurantId())
                    .build();
        }).collect(Collectors.toList());
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

    public Double calculateTotal(List<CartItemDTO> items) {
        return items.stream().mapToDouble(CartItemDTO::getSubTotal).sum();
    }

    @Transactional
    public void addToCart(Long userId, Long productId, Integer quantity) {
        cartItemRepository.findByUserIdAndRestaurantProductId(userId, productId)
                .ifPresentOrElse(
                        item -> {
                            item.setQuantity(item.getQuantity() + quantity);
                            cartItemRepository.save(item);
                        },
                        () -> {
                            CartItem newItem = CartItem.builder()
                                    .userId(userId)
                                    .restaurantProductId(productId)
                                    .quantity(quantity)
                                    .build();
                            cartItemRepository.save(newItem);
                        }
                );
    }

    public long getCartCount(Long userId) {
        return cartItemRepository.countByUserId(userId);
    }

    @Transactional
    public void removeFromCart(Long userId, Long productId) {
        cartItemRepository.deleteByUserIdAndRestaurantProductId(userId, productId);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    @Transactional
    public void updateQuantity(Long userId, Long productId, Integer quantity) {
        RestaurantProduct rp = restaurantProductRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        if (quantity > rp.getStockQuantity()) {
            throw new RuntimeException("Số lượng trong kho không đủ (Còn lại: " + rp.getStockQuantity() + ")");
        }

        cartItemRepository.findByUserIdAndRestaurantProductId(userId, productId)
                .ifPresent(item -> {
                    if (quantity <= 0) {
                        cartItemRepository.delete(item);
                    } else {
                        item.setQuantity(quantity);
                        cartItemRepository.save(item);
                    }
                });
    }
}