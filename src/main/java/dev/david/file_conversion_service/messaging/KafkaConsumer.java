package dev.david.file_conversion_service.messaging;

import dev.david.file_conversion_service.dto.FileConversionRequestedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(
            topics = "file.conversion.requested",
            groupId = "file-conversion-service")
    public void listener(FileConversionRequestedEvent event){
        System.out.println("=== Received event ===");
        System.out.println("eventId: " + event.eventId());
        System.out.println("filePath: " + event.filePath());
        System.out.println("originalFileName: " + event.originalFileName());
    }
}
