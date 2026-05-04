package kpi.zakrevskyi.neurolib.domain.dto.response;

public record BookRecommendationSourceDto(
    String bookId,
    String title,
    String authors,
    String genre,
    Integer publicationYear
) {
}
