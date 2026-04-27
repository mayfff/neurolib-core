package kpi.zakrevskyi.neurolib.domain.dto.response;

public record AuthResponseDto(
    String accessToken,
    String tokenType,
    long accessTokenExpiresInMs,
    String refreshToken,
    long refreshTokenExpiresInMs
) {
}
