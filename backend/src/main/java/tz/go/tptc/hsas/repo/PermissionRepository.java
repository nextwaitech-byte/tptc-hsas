package tz.go.tptc.hsas.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.go.tptc.hsas.domain.Permission;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Short> {
    Optional<Permission> findByName(String name);
}
