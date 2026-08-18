package tz.go.tptc.hsas.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.go.tptc.hsas.domain.PublicCommunication;
import java.util.List;

public interface PublicCommunicationRepository extends JpaRepository<PublicCommunication, Integer> {
    List<PublicCommunication> findAllByOrderByCreatedAtDesc();
}
