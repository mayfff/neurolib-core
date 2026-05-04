package kpi.zakrevskyi.neurolib.service;

import kpi.zakrevskyi.neurolib.domain.dto.response.BookRecommendationResponseDto;
import java.util.UUID;

public interface BookRecommendationService {
    int reindexBooks();

    void upsertBook(UUID bookId);

    void removeBook(UUID bookId);

    BookRecommendationResponseDto recommend(String query);
}
