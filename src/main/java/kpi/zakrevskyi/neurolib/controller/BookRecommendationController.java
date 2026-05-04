package kpi.zakrevskyi.neurolib.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.Map;
import kpi.zakrevskyi.neurolib.domain.dto.request.BookRecommendationRequestDto;
import kpi.zakrevskyi.neurolib.domain.dto.response.BookRecommendationResponseDto;
import kpi.zakrevskyi.neurolib.service.BookRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/books")
@RequiredArgsConstructor
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
        @Valid @RequestBody BookRecommendationRequestDto request
    ) {
        return ResponseEntity.ok(bookRecommendationService.recommend(request.query()));
    }
}
