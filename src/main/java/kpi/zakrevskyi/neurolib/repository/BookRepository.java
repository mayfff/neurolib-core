package kpi.zakrevskyi.neurolib.repository;

import kpi.zakrevskyi.neurolib.domain.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
}
