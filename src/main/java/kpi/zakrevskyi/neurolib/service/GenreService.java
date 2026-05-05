package kpi.zakrevskyi.neurolib.service;

import java.util.Set;
import java.util.UUID;
import kpi.zakrevskyi.neurolib.domain.dto.response.GenreResponseDto;

public interface GenreService {
    GenreResponseDto create(String title);

    GenreResponseDto getById(UUID id);

    Set<GenreResponseDto> getAll();

    GenreResponseDto update(UUID id, String title);

    String delete(UUID id);
}
