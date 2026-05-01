package kpi.zakrevskyi.neurolib.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record UpdateUserRequestDto(
    @NotBlank
    @Size(max = 100)
    String fullName,

    @NotBlank
    @Size(max = 30)
    String username,

    @Size(min = 8, max = 255)
    String currentPassword,

    @Size(min = 8, max = 255)
    String newPassword,

    @Size(min = 8, max = 255)
    String confirmPassword,

    MultipartFile profileImage
) {
}
