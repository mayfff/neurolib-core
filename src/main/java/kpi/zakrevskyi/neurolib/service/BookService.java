package kpi.zakrevskyi.neurolib.service;

import kpi.zakrevskyi.neurolib.domain.dto.request.BookRequestDto;
import kpi.zakrevskyi.neurolib.domain.dto.response.BookResponseDto;

import java.util.Set;
import java.util.UUID;

public interface BookService {
    BookResponseDto create(BookRequestDto request);

    BookResponseDto getById(UUID id);

    Set<BookResponseDto> getAll();

    BookResponseDto update(UUID id, BookRequestDto request);

    String delete(UUID id);

    BookResponseDto toggleLike(UUID bookId, String userEmail);

    BookResponseDto toggleDislike(UUID bookId, String userEmail);

    BookResponseDto toggleSave(UUID bookId, String userEmail);
}
