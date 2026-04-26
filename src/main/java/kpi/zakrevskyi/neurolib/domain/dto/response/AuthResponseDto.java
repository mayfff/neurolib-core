package kpi.zakrevskyi.neurolib.domain.dto.response;

public record AuthResponseDto(
    String token,
    String tokenType,
    long expiresInMs
) {
}
