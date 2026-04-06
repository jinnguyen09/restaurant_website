package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restaurant.entity.Point;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Point p WHERE p.user.userId = :userId")
    int getTotalPointsByUserId(@Param("userId") Long userId);
}