package restaurant.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long orderItemId;
    private Long orderId;
    private Integer rating;
    private String comment;
}
