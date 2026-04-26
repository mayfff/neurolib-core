package kpi.zakrevskyi.neurolib.domain.dto.response;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record BookResponseDto(
    UUID id,
    String title,
    String description,
    String coverImageUrl,
    GenreResponseDto genre,
    String pdfUrl,
    Integer publicationYear,
    LocalDateTime createdAt,
    Set<AuthorResponseDto> authors
) {
}
