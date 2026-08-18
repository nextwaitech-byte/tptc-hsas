package tz.go.tptc.hsas.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.go.tptc.hsas.domain.EarlyWarningIndicator;
import java.util.List;

public interface EarlyWarningIndicatorRepository extends JpaRepository<EarlyWarningIndicator, Integer> {
    List<EarlyWarningIndicator> findByActiveTrueOrderByIdAsc();
}
