package kpi.zakrevskyi.neurolib.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

public record BookRequestDto(
    @NotBlank
    @Size(max = 255)
    String title,

    @Size(max = 5000)
    String description,

    @Size(max = 512)
    String coverImageUrl,

    @NotNull
    UUID genreId,

    @Size(max = 1024)
    String pdfUrl,

    @NotNull
    Integer publicationYear,

    @NotNull
    Set<UUID> authorIds
) {
}
