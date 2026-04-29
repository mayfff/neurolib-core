package kpi.zakrevskyi.neurolib.service.implementation;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import kpi.zakrevskyi.neurolib.domain.dto.request.CommentRequestDto;
import kpi.zakrevskyi.neurolib.domain.dto.response.CommentResponseDto;
import kpi.zakrevskyi.neurolib.domain.entity.Book;
import kpi.zakrevskyi.neurolib.domain.entity.Comment;
import kpi.zakrevskyi.neurolib.domain.entity.Role;
import kpi.zakrevskyi.neurolib.domain.entity.User;
import kpi.zakrevskyi.neurolib.repository.BookRepository;
import kpi.zakrevskyi.neurolib.repository.CommentRepository;
import kpi.zakrevskyi.neurolib.service.CommentService;
import kpi.zakrevskyi.neurolib.service.UserService;
import kpi.zakrevskyi.neurolib.service.exception.AccessDeniedException;
import kpi.zakrevskyi.neurolib.service.exception.NotFoundException;
import kpi.zakrevskyi.neurolib.service.mappers.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;
    private final UserService userService;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentResponseDto create(UUID bookId, CommentRequestDto request, String userEmail) {
        Book book = findBookOrThrow(bookId);
        User user = findUserByEmailOrThrow(userEmail);

        Comment comment = new Comment();
        comment.setBook(book);
        comment.setUser(user);
        comment.setText(request.text());

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<CommentResponseDto> getAllByBook(UUID bookId) {
        findBookOrThrow(bookId);
        return commentRepository.findAllByBookIdOrderByCreatedAtDesc(bookId).stream()
            .map(commentMapper::toDto)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    @Transactional
    public CommentResponseDto update(UUID bookId, UUID commentId, CommentRequestDto request, String userEmail) {
        Comment comment = findCommentOrThrow(commentId);
        if (!comment.getBook().getId().equals(bookId)) {
            throw new NotFoundException("Comment with id [%s] not found for book [%s]".formatted(commentId, bookId));
        }

        User user = findUserByEmailOrThrow(userEmail);
        if (!comment.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You can update only your own comment");
        }

        comment.setText(request.text());
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public String delete(UUID bookId, UUID commentId, String userEmail) {
        Comment comment = findCommentOrThrow(commentId);
        if (!comment.getBook().getId().equals(bookId)) {
            throw new NotFoundException("Comment with id [%s] not found for book [%s]".formatted(commentId, bookId));
        }

        User user = findUserByEmailOrThrow(userEmail);
        if (!comment.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You can delete only your own comment");
        }

        commentRepository.delete(comment);
        return "Comment with id [%s] deleted".formatted(commentId);
    }

    private Book findBookOrThrow(UUID bookId) {
        return bookRepository.findById(bookId)
            .orElseThrow(() -> new NotFoundException("Book with id [%s] not found".formatted(bookId)));
    }

    private Comment findCommentOrThrow(UUID commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new NotFoundException("Comment with id [%s] not found".formatted(commentId)));
    }

    private User findUserByEmailOrThrow(String email) {
        return userService.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("User with email [%s] not found".formatted(email)));
    }
}
