package kpi.zakrevskyi.neurolib.service.implementation;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import kpi.zakrevskyi.neurolib.domain.dto.request.BookRequestDto;
import kpi.zakrevskyi.neurolib.domain.dto.response.BookResponseDto;
import kpi.zakrevskyi.neurolib.domain.entity.Author;
import kpi.zakrevskyi.neurolib.domain.entity.Book;
import kpi.zakrevskyi.neurolib.domain.entity.Genre;
import kpi.zakrevskyi.neurolib.domain.entity.User;
import kpi.zakrevskyi.neurolib.repository.AuthorRepository;
import kpi.zakrevskyi.neurolib.repository.BookRepository;
import kpi.zakrevskyi.neurolib.repository.GenreRepository;
import kpi.zakrevskyi.neurolib.service.BookService;
import kpi.zakrevskyi.neurolib.service.UserService;
import kpi.zakrevskyi.neurolib.service.exception.NotFoundException;
import kpi.zakrevskyi.neurolib.service.mappers.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final UserService userService;

    @Override
    @Transactional
    public BookResponseDto create(BookRequestDto request) {
        Genre genre = genreRepository.findById(request.genreId())
            .orElseThrow(() -> new NotFoundException("Genre with id [%s] not found".formatted(request.genreId())));

        Set<Author> authors = resolveAuthors(request.authorIds());

        Book book = new Book();
        book.setTitle(request.title());
        book.setDescription(request.description());
        book.setCoverImageUrl(request.coverImageUrl());
        book.setGenre(genre);
        book.setPdfUrl(request.pdfUrl());
        book.setPublicationYear(request.publicationYear());
        book.setAuthors(authors);

        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDto getById(UUID id) {
        return bookMapper.toDto(findBookOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<BookResponseDto> getAll() {
        return bookRepository.findAll().stream()
            .map(bookMapper::toDto)
            .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public BookResponseDto update(UUID id, BookRequestDto request) {
        Book book = findBookOrThrow(id);
        Genre genre = genreRepository.findById(request.genreId())
            .orElseThrow(() -> new NotFoundException("Genre with id [%s] not found".formatted(request.genreId())));
        Set<Author> authors = resolveAuthors(request.authorIds());

        book.setTitle(request.title());
        book.setDescription(request.description());
        book.setCoverImageUrl(request.coverImageUrl());
        book.setGenre(genre);
        book.setPdfUrl(request.pdfUrl());
        book.setPublicationYear(request.publicationYear());
        book.setAuthors(authors);

        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    @Transactional
    public String delete(UUID id) {
        if (!bookRepository.existsById(id)) {
            throw new NotFoundException("Book with id [%s] not found".formatted(id));
        }
        bookRepository.deleteById(id);
        return "Book with id [%s] deleted".formatted(id);
    }

    @Override
    @Transactional
    public BookResponseDto toggleLike(UUID bookId, String userEmail) {
        Book book = findBookOrThrow(bookId);
        User user = findUserByEmailOrThrow(userEmail);

        if (book.getDislikes().contains(user)) {
            book.getDislikes().remove(user);
            user.getDislikedBooks().remove(book);
        }

        if (book.getLikes().contains(user)) {
            book.getLikes().remove(user);
            user.getLikedBooks().remove(book);
        } else {
            book.getLikes().add(user);
            user.getLikedBooks().add(book);
        }

        return bookMapper.toDto(book);
    }

    @Override
    @Transactional
    public BookResponseDto toggleDislike(UUID bookId, String userEmail) {
        Book book = findBookOrThrow(bookId);
        User user = findUserByEmailOrThrow(userEmail);

        if (book.getLikes().contains(user)) {
            book.getLikes().remove(user);
            user.getLikedBooks().remove(book);
        }

        if (book.getDislikes().contains(user)) {
            book.getDislikes().remove(user);
            user.getDislikedBooks().remove(book);
        } else {
            book.getDislikes().add(user);
            user.getDislikedBooks().add(book);
        }

        return bookMapper.toDto(book);
    }

    @Override
    @Transactional
    public BookResponseDto toggleSave(UUID bookId, String userEmail) {
        Book book = findBookOrThrow(bookId);
        User user = findUserByEmailOrThrow(userEmail);

        if (book.getSavedBy().contains(user)) {
            book.getSavedBy().remove(user);
            user.getSavedBooks().remove(book);
        } else {
            book.getSavedBy().add(user);
            user.getSavedBooks().add(book);
        }

        return bookMapper.toDto(book);
    }

    private Book findBookOrThrow(UUID id) {
        return bookRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Book with id [%s] not found".formatted(id)));
    }

    private User findUserByEmailOrThrow(String email) {
        return userService.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("User with email [%s] not found".formatted(email)));
    }

    private Set<Author> resolveAuthors(Set<UUID> authorIds) {
        if (authorIds == null || authorIds.isEmpty()) {
            return new HashSet<>();
        }

        Set<Author> authors = new HashSet<>(authorRepository.findAllById(authorIds));
        if (authors.size() != authorIds.size()) {
            throw new NotFoundException("One or more authors not found");
        }
        return authors;
    }
}
