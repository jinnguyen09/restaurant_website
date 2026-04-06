package restaurant.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ranked")
@Data
public class Ranked {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rank_id;

    @Column(name = "rank_name", nullable = false)
    private String rankName;

    @Column(name = "min_points")
    private Integer minPoints;

    @Column(name = "discount_percent")
    private Double discountPercent;

    public Object getRankId() {
        return rank_id;
    }
}