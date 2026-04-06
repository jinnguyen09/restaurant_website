package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import restaurant.entity.Ranked;

import java.util.Optional;

@Repository
public interface RankRepository extends JpaRepository<Ranked, Integer> {
    Optional<Ranked> findFirstByMinPointsLessThanEqualOrderByMinPointsDesc(int points);
    Optional<Ranked> findFirstByMinPointsGreaterThanOrderByMinPointsAsc(int points);
}