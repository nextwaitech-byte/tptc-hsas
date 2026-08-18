package tz.go.tptc.hsas.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.go.tptc.hsas.domain.RefugeeCamp;
import java.util.List;

public interface RefugeeCampRepository extends JpaRepository<RefugeeCamp, Integer> {
    List<RefugeeCamp> findByActiveTrue();
}
