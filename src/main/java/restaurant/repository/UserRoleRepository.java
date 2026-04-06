package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import restaurant.entity.UserRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findAllByUser_UserId(Long userId);

    Optional<UserRole> findByUser_UserId(Long userId);

    void deleteByUser_UserId(Long userId);
}