package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restaurant.entity.Notification;
import restaurant.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId ORDER BY n.createAt DESC")
    List<Notification> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.userId = :userId AND n.isRead = false")
    long countUnreadNotifications(@Param("userId") Long userId);

    List<Notification> findByUserOrderByCreateAtDesc(User user);

    long countByUserAndIsReadFalse(User user);

    List<Notification> findAllUnreadByUser(User user);

    Page<Notification> findByUserOrderByCreateAtDesc(User user, Pageable pageable);
}