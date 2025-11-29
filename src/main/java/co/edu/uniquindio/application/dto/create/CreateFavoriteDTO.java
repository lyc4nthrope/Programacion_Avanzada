package co.edu.uniquindio.application.dto.create;

import jakarta.validation.constraints.NotBlank;

public record CreateFavoriteDTO(
        @NotBlank(message = "El ID del usuario es requerido")
        String userId,

        @NotBlank(message = "El ID del alojamiento es requerido")
        String accommodationId
) {
}