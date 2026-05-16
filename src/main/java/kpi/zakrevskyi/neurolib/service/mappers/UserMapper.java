package kpi.zakrevskyi.neurolib.service.mappers;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import kpi.zakrevskyi.neurolib.domain.dto.response.UserResponseDto;
import kpi.zakrevskyi.neurolib.domain.entity.Book;
import kpi.zakrevskyi.neurolib.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "likedBookIds", source = "likedBooks")
    @Mapping(target = "dislikedBookIds", source = "dislikedBooks")
    @Mapping(target = "savedBookIds", source = "savedBooks")
    @Mapping(target = "readingNowBookIds", source = "readingNowBooks")
    @Mapping(target = "readBookIds", source = "readBooks")
    UserResponseDto toDto(User user);

    default Set<UUID> mapBooksToIds(Set<Book> books) {
        if (books == null || books.isEmpty()) {
            return Collections.emptySet();
        }
        return books.stream()
            .map(Book::getId)
            .collect(Collectors.toSet());
    }
}
