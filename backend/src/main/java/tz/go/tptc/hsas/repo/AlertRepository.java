package tz.go.tptc.hsas.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tz.go.tptc.hsas.domain.Alert;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Integer> {
    @Query("select a from Alert a left join fetch a.borderPost order by a.createdAt desc")
    List<Alert> findAllDetailed();

    long countByStatusIn(List<String> statuses);
    long countByStatusInAndBorderPostId(List<String> statuses, Integer borderPostId);
}
