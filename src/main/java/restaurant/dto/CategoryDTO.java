package restaurant.dto;

import lombok.Data;

@Data
public class CategoryDTO {
    private Integer categoryId;
    private String categoryName;
    private String description;
    private Integer parentId;
}