package kpi.zakrevskyi.neurolib.controller;

import jakarta.validation.Valid;
import kpi.zakrevskyi.neurolib.domain.dto.response.BookResponseDto;
import kpi.zakrevskyi.neurolib.domain.dto.response.UserResponseDto;
import kpi.zakrevskyi.neurolib.domain.dto.request.UpdateUserRequestDto;
import kpi.zakrevskyi.neurolib.domain.entity.Role;
import kpi.zakrevskyi.neurolib.domain.entity.User;
import kpi.zakrevskyi.neurolib.service.UserService;
import kpi.zakrevskyi.neurolib.service.exception.AccessDeniedException;
import kpi.zakrevskyi.neurolib.service.exception.NotFoundException;
import kpi.zakrevskyi.neurolib.service.exception.UnauthorizedException;
import kpi.zakrevskyi.neurolib.service.mappers.BookMapper;
import kpi.zakrevskyi.neurolib.service.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    @Operation(summary = "Get user profile by id")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> profile(@PathVariable UUID id) {
        return ResponseEntity.ok(userMapper.toDto(findUserOrThrow(id)));
    }

    @Operation(summary = "Update current user profile")
    @PutMapping(value = "/{id}")
    public ResponseEntity<UserResponseDto> update(
        @PathVariable UUID id,
        @Valid @ModelAttribute UpdateUserRequestDto request,
        Authentication authentication
    ) {
        User currentUser = resolveCurrentUser(authentication);
        if (!currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You can update only your own profile");
        }

        User updatedUser = userService.update(id, request);
        return ResponseEntity.ok(userMapper.toDto(updatedUser));
    }

    @Operation(summary = "Get liked books of user")
    @GetMapping("/{id}/likes")
    public ResponseEntity<Set<BookResponseDto>> likes(@PathVariable UUID id) {
        User user = findUserOrThrow(id);
        return ResponseEntity.ok(bookMapper.toDtoSet(user.getLikedBooks()));
    }

    @Operation(summary = "Get disliked books of user")
    @GetMapping("/{id}/dislikes")
    public ResponseEntity<Set<BookResponseDto>> dislikes(@PathVariable UUID id) {
        User user = findUserOrThrow(id);
        return ResponseEntity.ok(bookMapper.toDtoSet(user.getDislikedBooks()));
    }

    @Operation(summary = "Get saved books of user")
    @GetMapping("/{id}/saved")
    public ResponseEntity<Set<BookResponseDto>> saved(@PathVariable UUID id) {
        User user = findUserOrThrow(id);
        return ResponseEntity.ok(bookMapper.toDtoSet(user.getSavedBooks()));
    }

    @Operation(summary = "Delete user profile")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id, Authentication authentication) {
        User currentUser = resolveCurrentUser(authentication);
        if (!currentUser.getId().equals(id) && currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You can delete only your own profile");
        }
        return ResponseEntity.ok(userService.delete(id));
    }

    @Operation(summary = "Delete user avatar")
    @DeleteMapping("/{id}/avatar")
    public ResponseEntity<String> deleteAvatar(@PathVariable UUID id, Authentication authentication) {
        User currentUser = resolveCurrentUser(authentication);
        if (!currentUser.getId().equals(id) && currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You can delete only your own avatar");
        }
        return ResponseEntity.ok(userService.deleteAvatar(id));
    }

    private User findUserOrThrow(UUID id) {
        return userService.findById(id)
            .orElseThrow(() -> new NotFoundException("User with id [%s] not found".formatted(id)));
    }

    private User resolveCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Unauthorized");
        }
        return userService.findByEmail(authentication.getName())
            .orElseThrow(() -> new UnauthorizedException("Unauthorized"));
    }
}
