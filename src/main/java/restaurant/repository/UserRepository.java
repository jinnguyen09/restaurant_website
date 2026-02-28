package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import restaurant.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    User findByEmailOrPhone(String email, String phone);
}