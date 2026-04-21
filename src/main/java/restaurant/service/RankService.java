package restaurant.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.entity.Ranked;
import restaurant.repository.RankRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankService {
    private final RankRepository rankRepository;

    public List<Ranked> getAllRanks() {
        return rankRepository.findAll();
    }
}