package tz.go.tptc.hsas.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tz.go.tptc.hsas.domain.Movement;
import java.time.Instant;
import java.util.List;

public interface MovementRepository extends JpaRepository<Movement, Long> {
    @Query("select m from Movement m join fetch m.borderPost join fetch m.officer where m.deletedAt is null order by m.crossedAt desc")
    List<Movement> findAllActive();

    long countByDirectionAndCrossedAtBetweenAndDeletedAtIsNull(String direction, Instant from, Instant to);
    long countByDirectionAndCrossedAtBetweenAndBorderPostIdAndDeletedAtIsNull(String direction, Instant from, Instant to, Integer borderPostId);
    long countByStatusAndDeletedAtIsNull(String status);
    long countByStatusAndBorderPostIdAndDeletedAtIsNull(String status, Integer borderPostId);
    long countByPurposeInAndDirectionAndCrossedAtGreaterThanEqualAndDeletedAtIsNull(List<String> purposes, String direction, Instant from);
    long countByDocumentNumberAndDeletedAtIsNullAndIdNot(String documentNumber, Long id);
    long countByNationalityAndOriginCountryAndCrossedAtGreaterThanEqualAndDeletedAtIsNull(String nationality, String originCountry, Instant from);
    long countByRiskScoreGreaterThanEqualAndDeletedAtIsNullAndBorderPostId(int score, Integer borderPostId);
}
