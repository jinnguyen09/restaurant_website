package restaurant.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "restaurants")
@Data
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Integer restaurantId;

    @Column(nullable = false)
    private String name;

    private String slogan;

    @Column(name = "restaurant_avatar")
    private String restaurantAvatar;

    @Column(name = "restaurant_img")
    private String restaurantImg;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String address;

    private String phone;

    private String email;

    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    private Integer capacity;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "map_url", columnDefinition = "TEXT")
    private String mapUrl;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = 1;
        }
    }
}