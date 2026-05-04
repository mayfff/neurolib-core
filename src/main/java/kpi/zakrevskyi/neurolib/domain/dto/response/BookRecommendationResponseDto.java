package kpi.zakrevskyi.neurolib.domain.dto.response;

import java.util.List;

public record BookRecommendationResponseDto(
    String answer,
    List<BookRecommendationSourceDto> sources
) {
}
