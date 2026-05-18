package kpi.zakrevskyi.neurolib.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import kpi.zakrevskyi.neurolib.domain.dto.response.AuthorResponseDto;
import kpi.zakrevskyi.neurolib.domain.dto.response.BookResponseDto;
import kpi.zakrevskyi.neurolib.domain.entity.Role;
import kpi.zakrevskyi.neurolib.domain.entity.User;
import kpi.zakrevskyi.neurolib.service.AuthorService;
import kpi.zakrevskyi.neurolib.service.BookService;
import kpi.zakrevskyi.neurolib.service.UserService;
import kpi.zakrevskyi.neurolib.service.exception.AccessDeniedException;
import kpi.zakrevskyi.neurolib.service.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
@Validated
public class AuthorController {
    private final AuthorService authorService;
    private final UserService userService;
    private final BookService bookService;

    @Operation(summary = "Create new author")
    @PostMapping
    public ResponseEntity<AuthorResponseDto> create(
        @RequestBody @NotBlank @Size(max = 255) String name,
        Authentication authentication
    ) {
        ensureAdmin(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.create(name));
    }

    @Operation(summary = "Get author by id")
    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(authorService.getById(id));
    }

    @Operation(summary = "Get all authors")
    @GetMapping
    public ResponseEntity<Set<AuthorResponseDto>> getAll() {
        return ResponseEntity.ok(authorService.getAll());
    }

    @Operation(summary = "Get books by author")
    @GetMapping("/{id}/books")
    public ResponseEntity<Set<BookResponseDto>> getBooksByAuthor(@PathVariable UUID id) {
        return ResponseEntity.ok(bookService.getByAuthorId(id));
    }

    @Operation(summary = "Update author by id")
    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponseDto> update(
        @PathVariable UUID id,
        @RequestBody @NotBlank @Size(max = 255) String name,
        Authentication authentication
    ) {
        ensureAdmin(authentication);
        return ResponseEntity.ok(authorService.update(id, name));
    }

    @Operation(summary = "Delete author by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id, Authentication authentication) {
        ensureAdmin(authentication);
        return ResponseEntity.ok(authorService.delete(id));
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
