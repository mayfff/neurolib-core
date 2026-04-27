package kpi.zakrevskyi.neurolib.service;

import java.util.Set;
import java.util.UUID;
import kpi.zakrevskyi.neurolib.domain.dto.request.AuthorRequestDto;
import kpi.zakrevskyi.neurolib.domain.dto.response.AuthorResponseDto;

public interface AuthorService {
    AuthorResponseDto create(AuthorRequestDto request);

    AuthorResponseDto getById(UUID id);

    Set<AuthorResponseDto> getAll();

    AuthorResponseDto update(UUID id, AuthorRequestDto request);

    String delete(UUID id);
}
