package co.edu.uniquindio.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AvailabilityDTO(
        String id,
        String accommodationId,
        String accommodationTitle,
        LocalDate date,
        Boolean available,
        String reason,
        LocalDateTime createdAt
) {
}