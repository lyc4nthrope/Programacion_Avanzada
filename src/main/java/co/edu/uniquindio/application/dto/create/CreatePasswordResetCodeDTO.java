package co.edu.uniquindio.application.dto.create;

import jakarta.validation.constraints.NotBlank;

public record CreatePasswordResetCodeDTO(
        @NotBlank(message = "El ID del usuario es requerido")
        String userId
) {
}