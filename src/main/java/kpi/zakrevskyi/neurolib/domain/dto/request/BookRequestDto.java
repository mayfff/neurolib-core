package kpi.zakrevskyi.neurolib.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public record BookRequestDto(
    @NotBlank
    @Size(max = 255)
    String title,

    @Size(max = 5000)
    String description,

    MultipartFile coverImage,

    @NotNull
    UUID genreId,

    MultipartFile pdfFile,

    @NotNull
    Integer publicationYear,

    @NotNull
    Set<UUID> authorIds
) {
}
