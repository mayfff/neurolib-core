package kpi.zakrevskyi.neurolib.service;

import java.util.Set;
import java.util.UUID;
import kpi.zakrevskyi.neurolib.domain.dto.response.CommentResponseDto;

public interface CommentService {
    CommentResponseDto create(UUID bookId, String text, String userEmail);

    Set<CommentResponseDto> getAllByBook(UUID bookId);

    CommentResponseDto update(UUID bookId, UUID commentId, String text, String userEmail);

    String delete(UUID bookId, UUID commentId, String userEmail);
}
