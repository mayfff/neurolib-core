package kpi.zakrevskyi.neurolib.domain.dto.response;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record BookResponseDto(
    UUID id,
    String title,
    String description,
    String coverImageUrl,
    UUID genreId,
    String pdfUrl,
    Integer publicationYear,
    LocalDateTime createdAt,
    Set<UUID> authorIds,
    Set<UUID> commentIds,
    Set<UUID> likedUserIds,
    Set<UUID> dislikedUserIds,
    Set<UUID> savedUserIds
) {
}
