package kpi.zakrevskyi.neurolib.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
    @NotBlank
    @Email
    @Size(max = 100)
    String email,

    @NotBlank
    @Size(min = 8, max = 255)
    String password,

    @NotBlank
    @Size(min = 8, max = 255)
    String confirmPassword,

    @NotBlank
    @Size(max = 100)
    String fullName,

    @NotBlank
    @Size(max = 30)
    String username
) {

}
