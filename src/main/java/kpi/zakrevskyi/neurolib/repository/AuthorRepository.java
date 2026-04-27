package kpi.zakrevskyi.neurolib.repository;

import kpi.zakrevskyi.neurolib.domain.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthorRepository extends JpaRepository<Author, UUID> {
    boolean existsByNameIgnoreCase(String name);
}
