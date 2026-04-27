package kpi.zakrevskyi.neurolib.domain.dto.response;

import java.util.Set;
import java.util.UUID;

public record GenreResponseDto(
    UUID id,
    String title,
    Set<UUID> bookIds
) {
}
