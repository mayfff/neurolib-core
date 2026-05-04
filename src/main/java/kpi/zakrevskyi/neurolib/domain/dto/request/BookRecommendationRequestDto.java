package kpi.zakrevskyi.neurolib.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

public record BookRecommendationRequestDto(
    @NotBlank
    String query
) {
}
