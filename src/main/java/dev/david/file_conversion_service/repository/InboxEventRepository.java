package dev.david.file_conversion_service.repository;

import dev.david.file_conversion_service.model.InboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InboxEventRepository extends JpaRepository<InboxEvent, UUID> {
}
