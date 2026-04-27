package kpi.zakrevskyi.neurolib.service.mappers;

import java.util.UUID;
import kpi.zakrevskyi.neurolib.domain.dto.response.CommentResponseDto;
import kpi.zakrevskyi.neurolib.domain.entity.Book;
import kpi.zakrevskyi.neurolib.domain.entity.Comment;
import kpi.zakrevskyi.neurolib.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "userId", source = "user.id")
    CommentResponseDto toDto(Comment comment);

    default Book mapBook(UUID id) {
        if (id == null) {
            return null;
        }
        Book book = new Book();
        book.setId(id);
        return book;
    }

    default User mapUser(UUID id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}
