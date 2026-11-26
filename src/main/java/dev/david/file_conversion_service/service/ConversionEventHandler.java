package dev.david.file_conversion_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.david.file_conversion_service.dto.ConversionStatus;
import dev.david.file_conversion_service.dto.FileConversionCompletedEvent;
import dev.david.file_conversion_service.dto.FileConversionRequestedEvent;
import dev.david.file_conversion_service.dto.OutboxStatus;
import dev.david.file_conversion_service.model.InboxEvent;
import dev.david.file_conversion_service.model.OutboxEvent;
import dev.david.file_conversion_service.repository.InboxEventRepository;
import dev.david.file_conversion_service.repository.OutboxEventRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversionEventHandler {

    private final InboxEventRepository inboxEventRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final FileConversionService fileConversionService;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.conversion-completed}")
    private String completedTopic;

    @Transactional
    public void handle(FileConversionRequestedEvent event) {

        if (inboxEventRepository.existsById(event.eventId())) {
            return;
        }

        inboxEventRepository.save(new InboxEvent(event.eventId(), Instant.now()));

        FileConversionCompletedEvent completedEvent;
        try {
            String convertedKey = fileConversionService.convert(event.filePath(), event.originalFileName());

            completedEvent = new FileConversionCompletedEvent(
                    UUID.randomUUID(),
                    event.eventId(),
                    ConversionStatus.SUCCESS,
                    convertedKey,
                    null
            );
        } catch (Exception e) {
            log.error("Failed to convert file for eventId={}", event.eventId(), e);

            completedEvent = new FileConversionCompletedEvent(
                    UUID.randomUUID(),
                    event.eventId(),
                    ConversionStatus.FAILED,
                    null,
                    e.getMessage()
            );
        }
        try {
            String payload = objectMapper.writeValueAsString(completedEvent);
            OutboxEvent outboxEvent = new OutboxEvent(UUID.randomUUID(),
                    completedTopic,
                    event.eventId().toString(),
                    payload,
                    OutboxStatus.PENDING,
                    Instant.now());
            outboxEventRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize completed event", e);
        }
    }
}
