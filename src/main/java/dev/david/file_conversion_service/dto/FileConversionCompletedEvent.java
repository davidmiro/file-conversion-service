package dev.david.file_conversion_service.dto;

import java.util.UUID;

public record FileConversionCompletedEvent(
        UUID eventId,
        UUID originalEventId,
        ConversionStatus status,
        String convertedObjectKey,
        String errorMessage

) {
}
