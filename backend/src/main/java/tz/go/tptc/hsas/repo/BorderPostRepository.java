package tz.go.tptc.hsas.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.go.tptc.hsas.domain.BorderPost;
import java.util.List;

public interface BorderPostRepository extends JpaRepository<BorderPost, Integer> {
    List<BorderPost> findByActiveTrueAndDeletedAtIsNullOrderByNameAsc();
}
