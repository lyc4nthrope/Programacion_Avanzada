package co.edu.uniquindio.application.dto;

import java.math.BigDecimal;

public record AccommodationDTO(
        String id,
        String title,
        String description,
        String city,
        String address,
        Double latitude,
        Double longitude,
        BigDecimal pricePerNight,
        Integer maxCapacity,
        String amenities,
        Double averageRating,
        String hostId
) {
}