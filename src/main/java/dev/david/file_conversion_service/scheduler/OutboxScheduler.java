package dev.david.file_conversion_service.scheduler;


import dev.david.file_conversion_service.dto.OutboxStatus;
import dev.david.file_conversion_service.model.OutboxEvent;
import dev.david.file_conversion_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 2000)
    public void processOutboxMessages() {

        List<OutboxEvent> pendingEvents = outboxEventRepository.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        for (OutboxEvent event : pendingEvents) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getKey(), event.getPayload()).get();
                event.setStatus(OutboxStatus.SENT);
                outboxEventRepository.save(event);
            } catch (Exception e) {
                log.error("Failed to publish outbox event ID: {}", event.getId(), e);
            }

        }
    }
}
