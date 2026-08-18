package tz.go.tptc.hsas.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tz.go.tptc.hsas.domain.InstitutionalCapacity;
import java.util.List;

public interface InstitutionalCapacityRepository extends JpaRepository<InstitutionalCapacity, Integer> {
    @Query("select c from InstitutionalCapacity c left join fetch c.borderPost order by c.recordedAt desc")
    List<InstitutionalCapacity> findAllDetailed();
}
