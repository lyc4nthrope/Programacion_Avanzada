package co.edu.uniquindio.application.dto;

import co.edu.uniquindio.application.models.enums.AccommodationStatus;

public record AccommodationDTO(
        String id,
        String title,
        String description,
        String city,
        String address,
        Double latitude,
        Double longitude,
        Double pricePerNight,
        Integer maxCapacity,
        String amenities,
        Double averageRating,
        String hostId,
        AccommodationStatus status  // ✅ NUEVO CAMPO
) {
}