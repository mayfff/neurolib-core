package kpi.zakrevskyi.neurolib.service.mappers;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import kpi.zakrevskyi.neurolib.domain.dto.response.GenreResponseDto;
import kpi.zakrevskyi.neurolib.domain.entity.Book;
import kpi.zakrevskyi.neurolib.domain.entity.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    @Mapping(target = "bookIds", source = "books")
    GenreResponseDto toDto(Genre genre);

    default Set<UUID> mapBooksToIds(Set<Book> books) {
        if (books == null || books.isEmpty()) {
            return Collections.emptySet();
        }
        return books.stream()
            .map(Book::getId)
            .collect(Collectors.toSet());
    }

    default Set<Book> mapIdsToBooks(Set<UUID> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            return Collections.emptySet();
        }
        return bookIds.stream()
            .map(this::toBook)
            .collect(Collectors.toSet());
    }

    default Book toBook(UUID id) {
        if (id == null) {
            return null;
        }
        Book book = new Book();
        book.setId(id);
        return book;
    }
}
