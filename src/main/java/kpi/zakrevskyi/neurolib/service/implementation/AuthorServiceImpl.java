package kpi.zakrevskyi.neurolib.service.implementation;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import kpi.zakrevskyi.neurolib.domain.dto.request.AuthorRequestDto;
import kpi.zakrevskyi.neurolib.domain.dto.response.AuthorResponseDto;
import kpi.zakrevskyi.neurolib.domain.entity.Author;
import kpi.zakrevskyi.neurolib.repository.AuthorRepository;
import kpi.zakrevskyi.neurolib.service.AuthorService;
import kpi.zakrevskyi.neurolib.service.exception.ConflictException;
import kpi.zakrevskyi.neurolib.service.exception.NotFoundException;
import kpi.zakrevskyi.neurolib.service.mappers.AuthorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Override
    @Transactional
    public AuthorResponseDto create(AuthorRequestDto request) {
        Author author = new Author();
        author.setName(request.name());

        return authorMapper.toDto(authorRepository.save(author));
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorResponseDto getById(UUID id) {
        Author author = authorRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Author with id [%s] not found".formatted(id)));

        return authorMapper.toDto(author);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<AuthorResponseDto> getAll() {
        return authorRepository.findAll().stream()
            .map(authorMapper::toDto)
            .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public AuthorResponseDto update(UUID id, AuthorRequestDto request) {
        Author author = authorRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Author with id [%s] not found".formatted(id)));

        boolean nameChanged = !author.getName().equalsIgnoreCase(request.name());
        if (nameChanged && authorRepository.existsByNameIgnoreCase(request.name())) {
            throw new ConflictException("Author with name [%s] already exists".formatted(request.name()));
        }

        author.setName(request.name());

        return authorMapper.toDto(authorRepository.save(author));
    }

    @Override
    @Transactional
    public String delete(UUID id) {
        if (!authorRepository.existsById(id)) {
            throw new NotFoundException("Author with id [%s] not found".formatted(id));
        }
        authorRepository.deleteById(id);
        return "Author with id [%s] deleted".formatted(id);
    }
}
