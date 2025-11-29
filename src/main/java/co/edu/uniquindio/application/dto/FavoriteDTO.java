package co.edu.uniquindio.application.dto;

import java.time.LocalDateTime;

public record FavoriteDTO(
        String id,
        String userId,
        String userName,
        String accommodationId,
        String accommodationTitle,
        String accommodationCity,
        Double accommodationPrice,
        Double accommodationRating,
        LocalDateTime createdAt
) {
}