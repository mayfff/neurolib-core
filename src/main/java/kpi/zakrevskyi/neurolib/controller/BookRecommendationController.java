package kpi.zakrevskyi.neurolib.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import kpi.zakrevskyi.neurolib.domain.dto.response.BookRecommendationResponseDto;
import kpi.zakrevskyi.neurolib.domain.dto.response.ChatMessageResponseDto;
import kpi.zakrevskyi.neurolib.service.BookRecommendationService;
import kpi.zakrevskyi.neurolib.service.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/books")
@RequiredArgsConstructor
@Validated
public class BookRecommendationController {
    private final BookRecommendationService bookRecommendationService;

    @Operation(summary = "Reindex all books into vector store")
    @PostMapping("/index")
    public ResponseEntity<Map<String, Integer>> reindexBooks() {
        int indexed = bookRecommendationService.reindexBooks();
        return ResponseEntity.ok(Map.of("indexed", indexed));
    }

    @Operation(summary = "Get book recommendations by text query")
    @PostMapping("/recommendations")
    public ResponseEntity<BookRecommendationResponseDto> recommend(
        @RequestBody @NotBlank String query,
        Authentication authentication
    ) {
        return ResponseEntity.ok(bookRecommendationService.recommend(query, resolveCurrentEmail(authentication)));
    }

    @Operation(summary = "Get current user chat history")
    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessageResponseDto>> getHistory(Authentication authentication) {
        return ResponseEntity.ok(bookRecommendationService.getHistory(resolveCurrentEmail(authentication)));
    }

    @Operation(summary = "Clear current user chat history")
    @DeleteMapping("/messages")
    public ResponseEntity<String> clearHistory(Authentication authentication) {
        bookRecommendationService.clearHistory(resolveCurrentEmail(authentication));
        return ResponseEntity.ok("Chat history cleared");
    }

    private String resolveCurrentEmail(Authentication authentication) {
        if (authentication == null
            || !authentication.isAuthenticated()
            || authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException("Unauthorized");
        }
        return authentication.getName();
    }
}
