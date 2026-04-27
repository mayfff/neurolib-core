package kpi.zakrevskyi.neurolib.repository;

import java.util.Optional;
import java.util.UUID;
import kpi.zakrevskyi.neurolib.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
}
