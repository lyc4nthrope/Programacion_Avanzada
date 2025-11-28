package co.edu.uniquindio.application.dto.edit;

import co.edu.uniquindio.application.models.enums.ReservationStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record EditReservationDTO(
        @NotNull(message = "La fecha de entrada es requerida")
        @FutureOrPresent(message = "La fecha de entrada no puede ser en el pasado")
        LocalDate checkInDate,

        @NotNull(message = "La fecha de salida es requerida")
        @FutureOrPresent(message = "La fecha de salida no puede ser en el pasado")
        LocalDate checkOutDate,

        @NotNull(message = "El número de huéspedes es requerido")
        @Positive(message = "El número de huéspedes debe ser mayor a 0")
        Integer numberOfGuests,

        @NotNull(message = "El estado es requerido")
        ReservationStatus status
) {
}