package kpi.zakrevskyi.neurolib.domain.dto.response;

import java.util.UUID;

public record AuthorResponseDto(
    UUID id,
    String name,
    String bio
) {
}
