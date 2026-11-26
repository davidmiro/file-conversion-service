package dev.david.file_conversion_service.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.david.file_conversion_service.dto.FileConversionRequestedEvent;
import dev.david.file_conversion_service.service.ConversionEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileConversionConsumer {

    private final ConversionEventHandler conversionEventHandler;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${spring.kafka.topics.conversion-requested}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(String message) {
        try {
            FileConversionRequestedEvent event = objectMapper.readValue(message, FileConversionRequestedEvent.class);
            log.info("Received conversion request: eventId={}, filePath={}", event.eventId(), event.filePath());
            conversionEventHandler.handle(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse Kafka message: {}", message, e);
        }
    }
}
