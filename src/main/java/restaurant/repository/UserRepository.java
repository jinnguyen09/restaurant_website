package restaurant.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import restaurant.dto.CustomerDTO;
import restaurant.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    User findByEmailOrPhone(String email, String phone);
    Optional<User> findByEmail(String email);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.avatar = :avatarPath WHERE u.userId = :userId")
    void updateAvatar(@Param("userId") Long userId, @Param("avatarPath") String avatarPath);

    @Query("SELECT new restaurant.dto.CustomerDTO(" +
            "u.userId, u.fullName, u.email, u.phone, u.rank.rankName, u.points, u.createdAt, " +
            "0L, 0.0, u.createdAt, u.avatar, " +
            "(SELECT ur.role.name FROM UserRole ur WHERE ur.user = u AND ur.id = (SELECT MIN(ur3.id) FROM UserRole ur3 WHERE ur3.user = u)), " +
            "(SELECT ur2.restaurant.name FROM UserRole ur2 WHERE ur2.user = u AND ur2.restaurant IS NOT NULL AND ur2.id = (SELECT MIN(ur4.id) FROM UserRole ur4 WHERE ur4.user = u AND ur4.restaurant IS NOT NULL))" +
            ") " +
            "FROM User u " +
            "WHERE (:kw IS NULL OR u.fullName LIKE %:kw% OR u.email LIKE %:kw% OR u.phone LIKE %:kw%)")
    List<CustomerDTO> findAllCustomerStats(@Param("kw") String keyword);
}