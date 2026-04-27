package kpi.zakrevskyi.neurolib.service.mappers;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import kpi.zakrevskyi.neurolib.domain.dto.response.AuthorResponseDto;
import kpi.zakrevskyi.neurolib.domain.entity.Author;
import kpi.zakrevskyi.neurolib.domain.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    @Mapping(target = "bookIds", source = "books")
    AuthorResponseDto toDto(Author author);

    @Mapping(target = "books", source = "bookIds")
    Author toEntity(AuthorResponseDto authorResponseDto);

    Set<AuthorResponseDto> toDtoSet(Set<Author> authors);

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
