package tz.go.tptc.hsas.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.go.tptc.hsas.domain.BrandingAsset;
import java.util.Optional;

public interface BrandingAssetRepository extends JpaRepository<BrandingAsset, Integer> {
    Optional<BrandingAsset> findByAssetKey(String key);
}
