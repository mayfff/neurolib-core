package kpi.zakrevskyi.neurolib.repository;

import java.util.List;
import java.util.UUID;
import kpi.zakrevskyi.neurolib.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findAllByBookIdOrderByCreatedAtDesc(UUID bookId);
}
