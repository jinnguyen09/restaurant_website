package restaurant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restaurant.entity.RestaurantProduct;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantProductRepository extends JpaRepository<RestaurantProduct, Long> {

    Optional<RestaurantProduct> findByRestaurant_RestaurantIdAndProduct_ProductId(Integer restaurantId, Long productId);

    List<RestaurantProduct> findByRestaurant_RestaurantId(Integer branchId);

    @Query(value = "SELECT DISTINCT rp FROM RestaurantProduct rp " +
            "JOIN FETCH rp.product p " +
            "LEFT JOIN FETCH p.category c " +
            "WHERE rp.restaurant.restaurantId = :branchId " +
            "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:parentId IS NULL OR c.parent.categoryId = :parentId OR c.categoryId = :parentId) " +
            "AND (:categoryId IS NULL OR c.categoryId = :categoryId)",
            countQuery = "SELECT COUNT(DISTINCT rp.restaurantProductId) FROM RestaurantProduct rp " +
                    "JOIN rp.product p " +
                    "JOIN p.category c " +
                    "WHERE rp.restaurant.restaurantId = :branchId " +
                    "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                    "AND (:parentId IS NULL OR c.parent.categoryId = :parentId OR c.categoryId = :parentId) " +
                    "AND (:categoryId IS NULL OR c.categoryId = :categoryId)")
    Page<RestaurantProduct> findByBranchAndFilters(@Param("branchId") Integer branchId,
                                                   @Param("keyword") String keyword,
                                                   @Param("parentId") Integer parentId,
                                                   @Param("categoryId") Integer categoryId,
                                                   Pageable pageable);

    @Query("SELECT DISTINCT rp FROM RestaurantProduct rp " +
            "JOIN FETCH rp.product p " +
            "LEFT JOIN FETCH p.category " +
            "WHERE rp.restaurant.restaurantId = :branchId " +
            "ORDER BY rp.restaurantProductId DESC")
    List<RestaurantProduct> findAllWithProductInfo(@Param("branchId") Integer branchId);

    @Query("SELECT COUNT(rp) FROM RestaurantProduct rp " +
            "JOIN rp.product p " +
            "JOIN p.category c " +
            "WHERE rp.restaurant.restaurantId = :branchId " +
            "AND (c.parent.categoryId = :parentCatId OR c.categoryId = :parentCatId)")
    long countByBranchAndParentCategory(@Param("branchId") Integer branchId, @Param("parentCatId") Integer parentCatId);

    @Query("SELECT COUNT(rp) FROM RestaurantProduct rp " +
            "JOIN rp.product p " +
            "JOIN p.category c " +
            "WHERE rp.restaurant.restaurantId = :branchId " +
            "AND (c.parent.categoryId = :parentCatId OR c.categoryId = :parentCatId) " +
            "AND rp.isAvailable = true AND rp.stockQuantity > 0")
    long countInStock(@Param("branchId") Integer branchId, @Param("parentCatId") Integer parentCatId);

    @Query("SELECT SUM(rp.stockQuantity) FROM RestaurantProduct rp " +
            "JOIN rp.product p " +
            "JOIN p.category c " +
            "WHERE rp.restaurant.restaurantId = :branchId " +
            "AND (c.parent.categoryId = :parentCatId OR c.categoryId = :parentCatId)")
    Long sumStockQuantity(@Param("branchId") Integer branchId, @Param("parentCatId") Integer parentCatId);
}