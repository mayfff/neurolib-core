package kpi.zakrevskyi.neurolib.service.implementation;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import kpi.zakrevskyi.neurolib.domain.dto.response.GenreResponseDto;
import kpi.zakrevskyi.neurolib.domain.entity.Genre;
import kpi.zakrevskyi.neurolib.repository.GenreRepository;
import kpi.zakrevskyi.neurolib.service.GenreService;
import kpi.zakrevskyi.neurolib.service.exception.ConflictException;
import kpi.zakrevskyi.neurolib.service.exception.NotFoundException;
import kpi.zakrevskyi.neurolib.service.mappers.GenreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Override
    @Transactional
    public GenreResponseDto create(String title) {
        if (genreRepository.existsByTitleIgnoreCase(title)) {
            throw new ConflictException("Genre with title [%s] already exists".formatted(title));
        }

        Genre genre = new Genre();
        genre.setTitle(title);

        return genreMapper.toDto(genreRepository.save(genre));
    }

    @Override
    @Transactional(readOnly = true)
    public GenreResponseDto getById(UUID id) {
        Genre genre = genreRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Genre with id [%s] not found".formatted(id)));

        return genreMapper.toDto(genre);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<GenreResponseDto> getAll() {
        return genreRepository.findAll().stream()
            .map(genreMapper::toDto)
            .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public GenreResponseDto update(UUID id, String title) {
        Genre genre = genreRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Genre with id [%s] not found".formatted(id)));

        boolean titleChanged = !genre.getTitle().equalsIgnoreCase(title);
        if (titleChanged && genreRepository.existsByTitleIgnoreCase(title)) {
            throw new ConflictException("Genre with title [%s] already exists".formatted(title));
        }

        genre.setTitle(title);
        return genreMapper.toDto(genreRepository.save(genre));
    }

    @Override
    @Transactional
    public String delete(UUID id) {
        if (!genreRepository.existsById(id)) {
            throw new NotFoundException("Genre with id [%s] not found".formatted(id));
        }
        genreRepository.deleteById(id);
        return "Genre with id [%s] deleted".formatted(id);
    }
}
