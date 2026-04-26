package kpi.zakrevskyi.neurolib.service.mappers;

import kpi.zakrevskyi.neurolib.domain.dto.response.GenreResponseDto;
import kpi.zakrevskyi.neurolib.domain.entity.Genre;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    GenreResponseDto toDto(Genre genre);

    Genre toEntity(GenreResponseDto genreResponseDto);
}
