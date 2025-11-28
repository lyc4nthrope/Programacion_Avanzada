package co.edu.uniquindio.application.dto;

import co.edu.uniquindio.application.models.enums.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservationDTO(
        String id,
        String accommodationId,
        String accommodationTitle,
        String guestId,
        String guestName,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer numberOfGuests,
        Double totalPrice,
        ReservationStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}