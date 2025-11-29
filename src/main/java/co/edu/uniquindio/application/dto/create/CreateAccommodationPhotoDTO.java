package co.edu.uniquindio.application.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAccommodationPhotoDTO(
        @NotBlank(message = "El ID del alojamiento es requerido")
        String accommodationId,
        
        @NotBlank(message = "La URL de la imagen es requerida")
        String imageUrl,
        
        @NotNull(message = "El campo isPrimary es requerido")
        Boolean isPrimary,
        
        @NotNull(message = "El orden de visualización es requerido")
        Integer displayOrder
) {
}