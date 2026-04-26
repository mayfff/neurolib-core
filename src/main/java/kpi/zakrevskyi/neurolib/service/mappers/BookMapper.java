package kpi.zakrevskyi.neurolib.service.mappers;

import java.util.Set;
import kpi.zakrevskyi.neurolib.domain.dto.response.BookResponseDto;
import kpi.zakrevskyi.neurolib.domain.entity.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AuthorMapper.class, GenreMapper.class})
public interface BookMapper {
    BookResponseDto toDto(Book book);

    Book toEntity(BookResponseDto bookResponseDto);

    Set<BookResponseDto> toDtoSet(Set<Book> books);

    Set<Book> toEntitySet(Set<BookResponseDto> bookResponseDtos);
}
