package kpi.zakrevskyi.neurolib.controller;

import jakarta.validation.Valid;
import java.util.Set;
import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import kpi.zakrevskyi.neurolib.domain.dto.request.GenreRequestDto;
import kpi.zakrevskyi.neurolib.domain.dto.response.GenreResponseDto;
import kpi.zakrevskyi.neurolib.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @Operation(summary = "Create new genre")
    @PostMapping
    public ResponseEntity<GenreResponseDto> create(@Valid @RequestBody GenreRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(genreService.create(request));
    }

    @Operation(summary = "Get genre by id")
    @GetMapping("/{id}")
    public ResponseEntity<GenreResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(genreService.getById(id));
    }

    @Operation(summary = "Get all genres")
    @GetMapping
    public ResponseEntity<Set<GenreResponseDto>> getAll() {
        return ResponseEntity.ok(genreService.getAll());
    }

    @Operation(summary = "Update genre by id")
    @PutMapping("/{id}")
    public ResponseEntity<GenreResponseDto> update(@PathVariable UUID id, @Valid @RequestBody GenreRequestDto request) {
        return ResponseEntity.ok(genreService.update(id, request));
    }

    @Operation(summary = "Delete genre by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(genreService.delete(id));
    }
}
