package tz.go.tptc.hsas.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tz.go.tptc.hsas.domain.CommunityReport;
import java.util.List;

public interface CommunityReportRepository extends JpaRepository<CommunityReport, Integer> {
    @Query("select c from CommunityReport c left join fetch c.createdBy order by c.reportedAt desc")
    List<CommunityReport> findAllDetailed();
}
