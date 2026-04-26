package kpi.zakrevskyi.neurolib.service.mappers;

import java.util.Set;
import kpi.zakrevskyi.neurolib.domain.dto.response.AuthorResponseDto;
import kpi.zakrevskyi.neurolib.domain.entity.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    AuthorResponseDto toDto(Author author);

    Author toEntity(AuthorResponseDto authorResponseDto);

    Set<AuthorResponseDto> toDtoSet(Set<Author> authors);

    Set<Author> toEntitySet(Set<AuthorResponseDto> authorResponseDtos);
}
