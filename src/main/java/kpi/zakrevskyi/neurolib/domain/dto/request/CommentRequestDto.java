package kpi.zakrevskyi.neurolib.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequestDto(
    @NotBlank
    @Size(max = 5000)
    String text
) {
}
