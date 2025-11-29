package co.edu.uniquindio.application.dto;

import java.time.LocalDateTime;

public record AccommodationPhotoDTO(
        String id,
        String accommodationId,
        String imageUrl,
        Boolean isPrimary,
        Integer displayOrder,
        LocalDateTime createdAt
) {
}