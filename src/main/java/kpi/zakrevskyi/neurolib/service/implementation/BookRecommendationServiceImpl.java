package kpi.zakrevskyi.neurolib.service.implementation;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import kpi.zakrevskyi.neurolib.domain.dto.response.BookRecommendationResponseDto;
import kpi.zakrevskyi.neurolib.domain.dto.response.ChatMessageResponseDto;
import kpi.zakrevskyi.neurolib.domain.entity.Author;
import kpi.zakrevskyi.neurolib.domain.entity.Book;
import kpi.zakrevskyi.neurolib.domain.entity.ChatMessage;
import kpi.zakrevskyi.neurolib.domain.entity.ChatMessageRole;
import kpi.zakrevskyi.neurolib.domain.entity.User;
import kpi.zakrevskyi.neurolib.repository.BookRepository;
import kpi.zakrevskyi.neurolib.repository.ChatMessageRepository;
import kpi.zakrevskyi.neurolib.repository.UserRepository;
import kpi.zakrevskyi.neurolib.service.BookRecommendationService;
import kpi.zakrevskyi.neurolib.service.exception.BadRequestException;
import kpi.zakrevskyi.neurolib.service.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Service;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class BookRecommendationServiceImpl implements BookRecommendationService {
    private static final int TOP_K = 3;

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public BookRecommendationServiceImpl(
        BookRepository bookRepository,
        UserRepository userRepository,
        ChatMessageRepository chatMessageRepository,
        VectorStore vectorStore,
        ChatClient.Builder chatClientBuilder
    ) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    @Transactional(readOnly = true)
    public int reindexBooks() {
        List<Book> books = bookRepository.findAllForRecommendation();
        if (books.isEmpty()) {
            return 0;
        }

        try {
            vectorStore.delete(books.stream().map(book -> book.getId().toString()).toList());
            List<Document> documents = books.stream()
                .map(this::toDocument)
                .toList();
            vectorStore.add(documents);
            return documents.size();
        } catch (RuntimeException ex) {
            throw new BadRequestException("Failed to reindex books");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void upsertBook(UUID bookId) {
        Book book = bookRepository.findByIdForRecommendation(bookId)
            .orElseThrow(() -> new NotFoundException("Book with id [%s] not found".formatted(bookId)));

        try {
            vectorStore.delete(List.of(bookId.toString()));
            vectorStore.add(List.of(toDocument(book)));
        } catch (RuntimeException ex) {
            throw new BadRequestException("Failed to update book index");
        }
    }

    @Override
    public void removeBook(UUID bookId) {
        try {
            vectorStore.delete(List.of(bookId.toString()));
        } catch (RuntimeException ex) {
            throw new BadRequestException("Failed to remove book from index");
        }
    }

    @Override
    @Transactional
    public BookRecommendationResponseDto recommend(String query, String userEmail) {
        if (!StringUtils.hasText(query)) {
            throw new BadRequestException("Query must not be blank");
        }
        User user = findUserByEmailOrThrow(userEmail);
        saveChatMessage(user, ChatMessageRole.USER, query.trim());

        List<Document> matches;
        try {
            matches = vectorStore.similaritySearch(
                SearchRequest.builder()
                    .query(query)
                    .topK(TOP_K)
                    .build()
            );
        } catch (RuntimeException ex) {
            log.error("Помилка пошуку рекомендацій для запиту '{}': {}", query, ex.getMessage(), ex);
            throw new BadRequestException("Failed to search recommendations");
        }

        String context = buildContext(matches);
        String answer;
        try {
            answer = chatClient.prompt()
                .system("""
                    Ти помічник бібліотеки. Рекомендуй книги лише на основі переданого списку.
                    Відповідай коротко й по суті: декілька рекомендацій і чому кожна підходить.
                    Якщо даних недостатньо, так і скажи, що на сайті немає необхідної книги.
                    """)
                .user("""
                    Запит користувача: %s

                    Доступні книги:
                    %s
                    """.formatted(query.trim(), context))
                .call()
                .content();
        } catch (RuntimeException ex) {
            if (isQuotaExceeded(ex)) {
                answer = "Досягнуто ліміт запитів до моделі. Спробуйте ще раз приблизно через хвилину.";
            } else if (isProviderTemporaryLimit(ex)) {
                answer = "Зараз сервіс рекомендацій перевантажений. Спробуйте ще раз за кілька секунд.";
            } else {
                answer = "Тимчасово не вдалося згенерувати рекомендації. Спробуйте ще раз трохи пізніше.";
                log.error("Помилка генерації рекомендацій для запиту '{}': {}", query, ex.getMessage(), ex);
            }
            saveChatMessage(user, ChatMessageRole.ASSISTANT, answer);
            return new BookRecommendationResponseDto(answer);
        }

        saveChatMessage(user, ChatMessageRole.ASSISTANT, answer);
        return new BookRecommendationResponseDto(answer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponseDto> getHistory(String userEmail) {
        User user = findUserByEmailOrThrow(userEmail);
        return chatMessageRepository.findAllByUserIdOrderByCreatedAtAsc(user.getId()).stream()
            .map(message -> new ChatMessageResponseDto(
                message.getRole().name(),
                message.getMessage(),
                message.getCreatedAt()
            ))
            .toList();
    }

    @Override
    @Transactional
    public void clearHistory(String userEmail) {
        User user = findUserByEmailOrThrow(userEmail);
        chatMessageRepository.deleteByUserId(user.getId());
    }

    private Document toDocument(Book book) {
        String authors = formatAuthors(book.getAuthors());
        String genre = book.getGenre() == null ? "" : book.getGenre().getTitle();
        String description = StringUtils.hasText(book.getDescription()) ? book.getDescription() : "";

        String text = """
            Назва: %s
            Автори: %s
            Жанр: %s
            Рік: %d
            Опис: %s
            """.formatted(
            book.getTitle(),
            authors,
            genre,
            book.getPublicationYear(),
            description
        );

        Map<String, Object> metadata = Map.of(
            "bookId", book.getId().toString(),
            "title", book.getTitle(),
            "authors", authors,
            "genre", genre,
            "publicationYear", book.getPublicationYear(),
            "description", description
        );

        return Document.builder()
            .id(book.getId().toString())
            .text(text)
            .metadata(metadata)
            .build();
    }

    private String buildContext(List<Document> matches) {
        return matches.stream()
            .map(document -> """
                - BookId: %s
                  Назва: %s
                  Автори: %s
                  Жанр: %s
                  Рік: %s
                  Опис: %s
                """.formatted(
                readMetadata(document, "bookId"),
                readMetadata(document, "title"),
                readMetadata(document, "authors"),
                readMetadata(document, "genre"),
                readMetadata(document, "publicationYear"),
                readMetadata(document, "description")
            ))
            .collect(Collectors.joining("\n"));
    }

    private String formatAuthors(Set<Author> authors) {
        if (authors == null || authors.isEmpty()) {
            return "Unknown";
        }
        return authors.stream()
            .map(Author::getName)
            .filter(StringUtils::hasText)
            .sorted()
            .collect(Collectors.joining(", "));
    }

    private User findUserByEmailOrThrow(String userEmail) {
        return userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new NotFoundException("User with email [%s] not found".formatted(userEmail)));
    }

    private void saveChatMessage(User user, ChatMessageRole role, String text) {
        ChatMessage message = new ChatMessage();
        message.setUser(user);
        message.setRole(role);
        message.setMessage(text);
        chatMessageRepository.save(message);
    }

    private String readMetadata(Document document, String key) {
        Object value = document.getMetadata().get(key);
        return value == null ? "" : String.valueOf(value);
    }

    private boolean isProviderTemporaryLimit(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            String message = current.getMessage();
            if (StringUtils.hasText(message)) {
                String lower = message.toLowerCase();
                boolean overloaded503 = lower.contains("503")
                    && (lower.contains("high demand") || lower.contains("overloaded"));
                if (overloaded503) {
                    return true;
                }
            }
            current = current.getCause();
        }
        return false;
    }

    private boolean isQuotaExceeded(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            String message = current.getMessage();
            if (StringUtils.hasText(message)) {
                String lower = message.toLowerCase();
                if (lower.contains("429")
                    && (lower.contains("quota") || lower.contains("rate limit") || lower.contains("too many requests"))) {
                    return true;
                }
            }
            current = current.getCause();
        }
        return false;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void syncOnStartup() {
        try {
            int indexedCount = reindexBooks();
            log.info("Books synced to vector store: {}", indexedCount);
        } catch (RuntimeException ex) {
            log.error("Failed to sync books to vector store on startup: {}", ex.getMessage());
        }
    }
}
