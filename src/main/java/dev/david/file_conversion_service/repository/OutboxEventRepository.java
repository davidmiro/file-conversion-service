package dev.david.file_conversion_service.repository;


import dev.david.file_conversion_service.dto.OutboxStatus;
import dev.david.file_conversion_service.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
