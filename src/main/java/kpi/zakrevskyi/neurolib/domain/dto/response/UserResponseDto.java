package kpi.zakrevskyi.neurolib.domain.dto.response;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserResponseDto(
    UUID id,
    String email,
    String fullName,
    String username,
    String profileImageUrl,
    LocalDateTime createdAt,
    Set<UUID> likedBookIds,
    Set<UUID> dislikedBookIds,
    Set<UUID> savedBookIds
) {
}
