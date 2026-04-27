package kpi.zakrevskyi.neurolib.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RefreshTokenRequestDto(
    @NotBlank
    @Size(max = 512)
    String refreshToken
) {
}
