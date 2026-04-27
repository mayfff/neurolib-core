package kpi.zakrevskyi.neurolib.service;

import java.util.Set;
import java.util.UUID;
import kpi.zakrevskyi.neurolib.domain.dto.request.GenreRequestDto;
import kpi.zakrevskyi.neurolib.domain.dto.response.GenreResponseDto;

public interface GenreService {
    GenreResponseDto create(GenreRequestDto request);

    GenreResponseDto getById(UUID id);

    Set<GenreResponseDto> getAll();

    GenreResponseDto update(UUID id, GenreRequestDto request);

    String delete(UUID id);
}
