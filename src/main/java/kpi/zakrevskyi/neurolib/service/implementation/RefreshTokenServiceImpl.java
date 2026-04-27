package kpi.zakrevskyi.neurolib.service.implementation;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import kpi.zakrevskyi.neurolib.domain.entity.RefreshToken;
import kpi.zakrevskyi.neurolib.domain.entity.User;
import kpi.zakrevskyi.neurolib.repository.RefreshTokenRepository;
import kpi.zakrevskyi.neurolib.service.RefreshTokenService;
import kpi.zakrevskyi.neurolib.service.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Override
    @Transactional
    public RefreshToken createToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(LocalDateTime.now().plus(Duration.ofMillis(refreshExpirationMs)));
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken validateToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (token.isRevoked()) {
            throw new UnauthorizedException("Refresh token is revoked");
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token is expired");
        }
        return token;
    }

    @Override
    @Transactional
    public void revokeToken(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public long getExpirationMs() {
        return refreshExpirationMs;
    }
}
