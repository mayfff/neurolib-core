package kpi.zakrevskyi.neurolib.service;

import kpi.zakrevskyi.neurolib.domain.entity.RefreshToken;
import kpi.zakrevskyi.neurolib.domain.entity.User;

public interface RefreshTokenService {
    RefreshToken createToken(User user);

    RefreshToken validateToken(String refreshToken);

    void revokeToken(RefreshToken refreshToken);

    long getExpirationMs();
}
