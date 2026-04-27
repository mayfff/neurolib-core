package kpi.zakrevskyi.neurolib.service.mappers;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import kpi.zakrevskyi.neurolib.domain.dto.response.BookResponseDto;
import kpi.zakrevskyi.neurolib.domain.entity.Author;
import kpi.zakrevskyi.neurolib.domain.entity.Book;
import kpi.zakrevskyi.neurolib.domain.entity.Comment;
import kpi.zakrevskyi.neurolib.domain.entity.Genre;
import kpi.zakrevskyi.neurolib.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mapping(target = "genreId", source = "genre.id")
    @Mapping(target = "authorIds", source = "authors")
    @Mapping(target = "commentIds", source = "comments")
    @Mapping(target = "likedUserIds", source = "likes")
    @Mapping(target = "dislikedUserIds", source = "dislikes")
    @Mapping(target = "savedUserIds", source = "savedBy")
    BookResponseDto toDto(Book book);

    @Mapping(target = "genre", source = "genreId")
    @Mapping(target = "authors", source = "authorIds")
    @Mapping(target = "comments", source = "commentIds")
    @Mapping(target = "likes", source = "likedUserIds")
    @Mapping(target = "dislikes", source = "dislikedUserIds")
    @Mapping(target = "savedBy", source = "savedUserIds")
    Book toEntity(BookResponseDto bookResponseDto);

    Set<BookResponseDto> toDtoSet(Set<Book> books);

    default Set<UUID> mapAuthorsToIds(Set<Author> authors) {
        if (authors == null || authors.isEmpty()) {
            return Collections.emptySet();
        }
        return authors.stream()
            .map(Author::getId)
            .collect(Collectors.toSet());
    }

    default Set<Author> mapAuthorIdsToAuthors(Set<UUID> authorIds) {
        if (authorIds == null || authorIds.isEmpty()) {
            return Collections.emptySet();
        }
        return authorIds.stream()
            .map(this::toAuthor)
            .collect(Collectors.toSet());
    }

    default Set<UUID> mapCommentsToIds(Set<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return Collections.emptySet();
        }
        return comments.stream()
            .map(Comment::getId)
            .collect(Collectors.toSet());
    }

    default Set<Comment> mapCommentIdsToComments(Set<UUID> commentIds) {
        if (commentIds == null || commentIds.isEmpty()) {
            return Collections.emptySet();
        }
        return commentIds.stream()
            .map(this::toComment)
            .collect(Collectors.toSet());
    }

    default Set<UUID> mapUsersToIds(Set<User> users) {
        if (users == null || users.isEmpty()) {
            return Collections.emptySet();
        }
        return users.stream()
            .map(User::getId)
            .collect(Collectors.toSet());
    }

    default Set<User> mapUserIdsToUsers(Set<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptySet();
        }
        return userIds.stream()
            .map(this::toUser)
            .collect(Collectors.toSet());
    }

    default Genre mapGenre(UUID id) {
        if (id == null) {
            return null;
        }
        Genre genre = new Genre();
        genre.setId(id);
        return genre;
    }

    default Author toAuthor(UUID id) {
        if (id == null) {
            return null;
        }
        Author author = new Author();
        author.setId(id);
        return author;
    }

    default Comment toComment(UUID id) {
        if (id == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setId(id);
        return comment;
    }

    default User toUser(UUID id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}
