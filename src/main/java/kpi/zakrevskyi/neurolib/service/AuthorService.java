package kpi.zakrevskyi.neurolib.service;

import java.util.Set;
import java.util.UUID;
import kpi.zakrevskyi.neurolib.domain.dto.response.AuthorResponseDto;

public interface AuthorService {
    AuthorResponseDto create(String name);

    AuthorResponseDto getById(UUID id);

    Set<AuthorResponseDto> getAll();

    AuthorResponseDto update(UUID id, String name);

    String delete(UUID id);
}
