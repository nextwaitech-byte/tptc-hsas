package tz.go.tptc.hsas.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tz.go.tptc.hsas.domain.Incident;
import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Integer> {
    @Query("select i from Incident i join fetch i.borderPost join fetch i.reportedBy order by i.occurredAt desc")
    List<Incident> findAllDetailed();

    long countByStatusIn(List<String> statuses);
    long countByStatusInAndBorderPostId(List<String> statuses, Integer borderPostId);
    long countByBorderPostIdAndStatusIn(Integer borderPostId, List<String> statuses);
}
