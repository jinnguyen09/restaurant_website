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

    @Query("SELECT DISTINCT new restaurant.dto.CustomerDTO(" +
            "u.userId, u.fullName, u.email, u.phone, u.rank.rankName, u.points, u.createdAt, " +
            "0L, 0.0, u.createdAt, u.avatar, " +
            "(SELECT ur.role.name FROM UserRole ur WHERE ur.user = u AND ur.id = (SELECT MIN(ur3.id) FROM UserRole ur3 WHERE ur3.user = u)), " +
            "(SELECT ur2.restaurant.name FROM UserRole ur2 WHERE ur2.user = u AND ur2.restaurant IS NOT NULL AND ur2.id = (SELECT MIN(ur4.id) FROM UserRole ur4 WHERE ur4.user = u AND ur4.restaurant IS NOT NULL))" +
            ") " +
            "FROM User u " +
            "JOIN u.userRoles ur " +
            "WHERE ur.role.name = 'ROLE_USER' " +
            "AND (:kw IS NULL OR u.fullName LIKE %:kw% OR u.email LIKE %:kw% OR u.phone LIKE %:kw%)")
    List<CustomerDTO> findAllCustomerStats(@Param("kw") String keyword);

    @Query("SELECT u FROM User u JOIN u.userRoles ur JOIN ur.role r WHERE r.name IN :roleNames")
    List<User> findUsersByRoleNames(@Param("roleNames") List<String> roleNames);

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN u.userRoles ur " +
            "WHERE ur.role.name IN :roleNames " +
            "AND ur.restaurant.restaurantId = :branchId")
    List<User> findInternalUsersByBranch(
            @Param("roleNames") List<String> roleNames,
            @Param("branchId") Integer branchId);

    @Query("SELECT DISTINCT new restaurant.dto.CustomerDTO(" +
            "u.userId, u.fullName, u.email, u.phone, u.rank.rankName, u.points, u.createdAt, " +
            "0L, 0.0, u.createdAt, u.avatar, " +
            "(SELECT ur.role.name FROM UserRole ur WHERE ur.user = u AND ur.id = (SELECT MIN(ur3.id) FROM UserRole ur3 WHERE ur3.user = u)), " +
            "(SELECT ur2.restaurant.name FROM UserRole ur2 WHERE ur2.user = u AND ur2.restaurant IS NOT NULL AND ur2.id = (SELECT MIN(ur4.id) FROM UserRole ur4 WHERE ur4.user = u AND ur4.restaurant IS NOT NULL))" +
            ") " +
            "FROM User u " +
            "JOIN u.userRoles ur " +
            "WHERE ur.role.name IN ('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_STAFF') " +
            "AND (" +
            "   :branchId IS NULL " +
            "   OR ur.restaurant.restaurantId = :branchId " +
            "   OR ur.role.name = 'ROLE_ADMIN'" +
            ") " +
            "AND (:kw IS NULL OR u.fullName LIKE %:kw% OR u.email LIKE %:kw% OR u.phone LIKE %:kw%)")
    List<CustomerDTO> findAllStaffStats(@Param("kw") String keyword, @Param("branchId") Integer branchId);

    @Query("SELECT u FROM User u WHERE u.email = :id OR u.phone = :id")
    Optional<User> findByEmailOrPhone(@Param("id") String identifier);

    @Query("SELECT u FROM User u WHERE u.email LIKE %:kw% OR u.phone LIKE %:kw%")
    List<User> searchByEmailOrPhone(@Param("kw") String keyword);

    @Query("SELECT u FROM User u WHERE u.rank.rankName = :rankName")
    List<User> findByRankName(@Param("rankName") String rankName);
}