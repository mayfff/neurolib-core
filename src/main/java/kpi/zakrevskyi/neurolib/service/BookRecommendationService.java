package kpi.zakrevskyi.neurolib.service;

import java.util.List;
import kpi.zakrevskyi.neurolib.domain.dto.response.BookRecommendationResponseDto;
import kpi.zakrevskyi.neurolib.domain.dto.response.ChatMessageResponseDto;
import java.util.UUID;

public interface BookRecommendationService {
    int reindexBooks();

    void upsertBook(UUID bookId);

    void removeBook(UUID bookId);

    BookRecommendationResponseDto recommend(String query, String userEmail);

    List<ChatMessageResponseDto> getHistory(String userEmail);

    void clearHistory(String userEmail);
}
