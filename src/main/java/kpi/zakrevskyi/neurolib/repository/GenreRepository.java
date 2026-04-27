package kpi.zakrevskyi.neurolib.repository;

import kpi.zakrevskyi.neurolib.domain.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GenreRepository extends JpaRepository<Genre, UUID> {
    boolean existsByTitleIgnoreCase(String title);
}
