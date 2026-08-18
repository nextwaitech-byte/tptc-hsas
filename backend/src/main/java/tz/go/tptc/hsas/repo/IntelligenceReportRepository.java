package tz.go.tptc.hsas.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tz.go.tptc.hsas.domain.IntelligenceReport;
import java.util.List;

public interface IntelligenceReportRepository extends JpaRepository<IntelligenceReport, Integer> {
    @Query("select r from IntelligenceReport r left join fetch r.borderPost join fetch r.reportedBy order by r.reportedAt desc")
    List<IntelligenceReport> findAllDetailed();

    List<IntelligenceReport> findByDomainOrderByReportedAtDesc(String domain);
    long countByStatusIn(List<String> statuses);
    long countByStatusInAndSeverity(List<String> statuses, String severity);
    long countByDomainAndStatusIn(String domain, List<String> statuses);
    long countByDomainAndCategoryInAndStatusIn(String domain, List<String> categories, List<String> statuses);
    long countByDomainAndStatusInAndSeverityIn(String domain, List<String> statuses, List<String> severities);
    long countByDomainInAndCategoryInAndStatusIn(List<String> domains, List<String> categories, List<String> statuses);
    long countByBorderPostIdAndStatusIn(Integer borderPostId, List<String> statuses);
    long countByDomainAndSeverityAndStatusIn(String domain, String severity, List<String> statuses);
}
