package kpi.zakrevskyi.neurolib.repository;

import java.util.List;
import java.util.UUID;
import kpi.zakrevskyi.neurolib.domain.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findAllByUserIdOrderByCreatedAtAsc(UUID userId);

    void deleteByUserId(UUID userId);
}
