package co.edu.uniquindio.application.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDate;

public record CreateAvailabilityDTO(
        @NotBlank(message = "El ID del alojamiento es requerido")
        String accommodationId,
        
        @NotNull(message = "La fecha es requerida")
        LocalDate date,
        
        @NotNull(message = "El estado de disponibilidad es requerido")
        Boolean available,
        
        @Length(max = 500, message = "La razón no puede exceder 500 caracteres")
        String reason
) {
}