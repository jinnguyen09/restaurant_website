package restaurant.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductBranchDTO {
    private Long productId;
    private String name;
    private String imageUrl;
    private String description;
    private Integer categoryId;
    private String categoryName;

    private String type;
    private String unit;
    private Integer preparationTime;

    private BigDecimal price;
    private Integer stockQuantity;
    private Boolean isAvailable;
    private Boolean isFeatured;

    private BigDecimal specialPrice;
    private String discountName;
}