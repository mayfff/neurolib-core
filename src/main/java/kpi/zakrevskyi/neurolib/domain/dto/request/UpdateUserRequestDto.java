package kpi.zakrevskyi.neurolib.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequestDto(
    @NotBlank
    @Email
    @Size(max = 100)
    String email,

    @NotBlank
    @Size(max = 100)
    String fullName,

    @NotBlank
    @Size(max = 30)
    String username,

    @Size(max = 512)
    String profileImageUrl
) {
}
