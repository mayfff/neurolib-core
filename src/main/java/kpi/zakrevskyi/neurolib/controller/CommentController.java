package kpi.zakrevskyi.neurolib.controller;

import jakarta.validation.Valid;
import java.util.Set;
import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import kpi.zakrevskyi.neurolib.domain.dto.request.CommentRequestDto;
import kpi.zakrevskyi.neurolib.domain.dto.response.CommentResponseDto;
import kpi.zakrevskyi.neurolib.service.CommentService;
import kpi.zakrevskyi.neurolib.service.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books/{bookId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Create comment for book")
    @PostMapping
    public ResponseEntity<CommentResponseDto> create(
        @PathVariable UUID bookId,
        @Valid @RequestBody CommentRequestDto request,
        Authentication authentication
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(commentService.create(bookId, request, resolveCurrentEmail(authentication)));
    }

    @Operation(summary = "Get all comments for book")
    @GetMapping
    public ResponseEntity<Set<CommentResponseDto>> getAllByBook(@PathVariable UUID bookId) {
        return ResponseEntity.ok(commentService.getAllByBook(bookId));
    }

    @Operation(summary = "Update own comment for book")
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> update(
        @PathVariable UUID bookId,
        @PathVariable UUID commentId,
        @Valid @RequestBody CommentRequestDto request,
        Authentication authentication
    ) {
        return ResponseEntity.ok(commentService.update(bookId, commentId, request, resolveCurrentEmail(authentication)));
    }

    @Operation(summary = "Delete own comment for book")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> delete(
        @PathVariable UUID bookId,
        @PathVariable UUID commentId,
        Authentication authentication
    ) {
        return ResponseEntity.ok(commentService.delete(bookId, commentId, resolveCurrentEmail(authentication)));
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
