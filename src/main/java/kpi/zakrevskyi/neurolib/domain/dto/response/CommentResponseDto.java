package kpi.zakrevskyi.neurolib.domain.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentResponseDto(
    UUID id,
    UUID bookId,
    UUID userId,
    String text,
    LocalDateTime createdAt
) {
}
