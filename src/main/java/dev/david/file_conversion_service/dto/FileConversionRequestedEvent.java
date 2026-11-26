package dev.david.file_conversion_service.dto;

import java.util.UUID;

public record FileConversionRequestedEvent(
        UUID eventId,
        String filePath,
        String originalFileName
) {
}