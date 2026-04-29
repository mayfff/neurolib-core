package kpi.zakrevskyi.neurolib.controller;

import jakarta.validation.Valid;
import java.util.Set;
import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import kpi.zakrevskyi.neurolib.domain.dto.request.BookRequestDto;
import kpi.zakrevskyi.neurolib.domain.dto.response.BookResponseDto;
import kpi.zakrevskyi.neurolib.domain.entity.Role;
import kpi.zakrevskyi.neurolib.domain.entity.User;
import kpi.zakrevskyi.neurolib.service.BookService;
import kpi.zakrevskyi.neurolib.service.UserService;
import kpi.zakrevskyi.neurolib.service.exception.AccessDeniedException;
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
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final UserService userService;

    @Operation(summary = "Create new book")
    @PostMapping
    public ResponseEntity<BookResponseDto> create(
        @Valid @RequestBody BookRequestDto request,
        Authentication authentication
    ) {
        ensureAdmin(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(request));
    }

    @Operation(summary = "Get book by id")
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(bookService.getById(id));
    }

    @Operation(summary = "Get all books")
    @GetMapping
    public ResponseEntity<Set<BookResponseDto>> getAll() {
        return ResponseEntity.ok(bookService.getAll());
    }

    @Operation(summary = "Update book by id")
    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDto> update(
        @PathVariable UUID id,
        @Valid @RequestBody BookRequestDto request,
        Authentication authentication
    ) {
        ensureAdmin(authentication);
        return ResponseEntity.ok(bookService.update(id, request));
    }

    @Operation(summary = "Delete book by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id, Authentication authentication) {
        ensureAdmin(authentication);
        return ResponseEntity.ok(bookService.delete(id));
    }

    @Operation(summary = "Toggle like on book")
    @PostMapping("/{id}/toggle-like")
    public ResponseEntity<BookResponseDto> toggleLike(@PathVariable UUID id, Authentication authentication) {
        return ResponseEntity.ok(bookService.toggleLike(id, resolveCurrentEmail(authentication)));
    }

    @Operation(summary = "Toggle dislike on book")
    @PostMapping("/{id}/toggle-dislike")
    public ResponseEntity<BookResponseDto> toggleDislike(@PathVariable UUID id, Authentication authentication) {
        return ResponseEntity.ok(bookService.toggleDislike(id, resolveCurrentEmail(authentication)));
    }

    @Operation(summary = "Toggle save on book")
    @PostMapping("/{id}/toggle-save")
    public ResponseEntity<BookResponseDto> toggleSave(@PathVariable UUID id, Authentication authentication) {
        return ResponseEntity.ok(bookService.toggleSave(id, resolveCurrentEmail(authentication)));
    }

    private String resolveCurrentEmail(Authentication authentication) {
        if (authentication == null
            || !authentication.isAuthenticated()
            || authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException("Unauthorized");
        }
        return authentication.getName();
    }

    private User resolveCurrentUser(Authentication authentication) {
        String email = resolveCurrentEmail(authentication);
        return userService.findByEmail(email).orElseThrow(() -> new UnauthorizedException("Unauthorized"));
    }

    private void ensureAdmin(Authentication authentication) {
        User user = resolveCurrentUser(authentication);
        if (user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException();
        }
    }
}
