package kpi.zakrevskyi.neurolib.domain.dto.response;

import java.time.LocalDateTime;

public record ChatMessageResponseDto(
    String role,
    String message,
    LocalDateTime createdAt
) {
}
