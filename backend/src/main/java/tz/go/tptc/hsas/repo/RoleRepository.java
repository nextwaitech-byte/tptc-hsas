package tz.go.tptc.hsas.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.go.tptc.hsas.domain.Role;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Short> {
    Optional<Role> findByName(String name);
}
