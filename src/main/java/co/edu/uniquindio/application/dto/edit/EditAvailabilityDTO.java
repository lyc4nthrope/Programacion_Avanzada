package co.edu.uniquindio.application.dto.edit;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record EditAvailabilityDTO(
        @NotNull(message = "El estado de disponibilidad es requerido")
        Boolean available,
        
        @Length(max = 500, message = "La razón no puede exceder 500 caracteres")
        String reason
) {
}