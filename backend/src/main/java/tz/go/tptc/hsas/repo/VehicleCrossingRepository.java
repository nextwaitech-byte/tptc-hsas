package tz.go.tptc.hsas.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tz.go.tptc.hsas.domain.VehicleCrossing;
import java.time.Instant;
import java.util.List;

public interface VehicleCrossingRepository extends JpaRepository<VehicleCrossing, Long> {
    @Query("select v from VehicleCrossing v join fetch v.borderPost join fetch v.officer where v.deletedAt is null order by v.crossedAt desc")
    List<VehicleCrossing> findAllActive();

    long countByCrossedAtBetweenAndDeletedAtIsNull(Instant from, Instant to);
    long countByCrossedAtBetweenAndBorderPostIdAndDeletedAtIsNull(Instant from, Instant to, Integer borderPostId);
    long countByHasCargoTrueAndCargoDeclaredFalseAndCrossedAtBetweenAndDeletedAtIsNull(Instant from, Instant to);
    long countByStatusInAndDeletedAtIsNull(List<String> statuses);
}
