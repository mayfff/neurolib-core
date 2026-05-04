package kpi.zakrevskyi.neurolib.service.implementation;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import kpi.zakrevskyi.neurolib.domain.dto.response.BookRecommendationResponseDto;
import kpi.zakrevskyi.neurolib.domain.dto.response.BookRecommendationSourceDto;
import kpi.zakrevskyi.neurolib.domain.entity.Author;
import kpi.zakrevskyi.neurolib.domain.entity.Book;
import kpi.zakrevskyi.neurolib.repository.BookRepository;
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
    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public BookRecommendationServiceImpl(
        BookRepository bookRepository,
        VectorStore vectorStore,
        ChatClient.Builder chatClientBuilder
    ) {
        this.bookRepository = bookRepository;
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
    public BookRecommendationResponseDto recommend(String query) {
        if (!StringUtils.hasText(query)) {
            throw new BadRequestException("Query must not be blank");
        }

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

        if (matches == null || matches.isEmpty()) {
            return new BookRecommendationResponseDto(
                "Не знайшов відповідних книг у каталозі. Уточніть запит: жанр, тему або автора.",
                List.of()
            );
        }

        String context = buildContext(matches);
        String answer;
        try {
            answer = chatClient.prompt()
                .system("""
                    Ти помічник бібліотеки. Рекомендуй книги лише на основі переданого списку.
                    Відповідай коротко й по суті: 2-5 рекомендацій і чому кожна підходить.
                    Якщо даних недостатньо, прямо про це скажи.
                    """)
                .user("""
                    Запит користувача: %s

                    Доступні книги:
                    %s
                    """.formatted(query.trim(), context))
                .call()
                .content();
        } catch (RuntimeException ex) {
            throw new BadRequestException("Failed to generate recommendations");
        }

        return new BookRecommendationResponseDto(answer, mapSources(matches));
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

    private List<BookRecommendationSourceDto> mapSources(List<Document> matches) {
        return matches.stream()
            .map(document -> new BookRecommendationSourceDto(
                readMetadata(document, "bookId"),
                readMetadata(document, "title"),
                readMetadata(document, "authors"),
                readMetadata(document, "genre"),
                readIntMetadata(document)
            ))
            .toList();
    }

    private String buildContext(List<Document> matches) {
        return matches.stream()
            .map(document -> """
                - Назва: %s
                  Автори: %s
                  Жанр: %s
                  Рік: %s
                  Опис: %s
                """.formatted(
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

    private String readMetadata(Document document, String key) {
        Object value = document.getMetadata().get(key);
        return value == null ? "" : String.valueOf(value);
    }

    private Integer readIntMetadata(Document document) {
        Object value = document.getMetadata().get("publicationYear");
        if (value instanceof Integer integer) {
            return integer;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String stringValue && StringUtils.hasText(stringValue)) {
            try {
                return Integer.parseInt(stringValue);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
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
