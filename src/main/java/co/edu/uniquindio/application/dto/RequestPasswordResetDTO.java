package co.edu.uniquindio.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RequestPasswordResetDTO(
        @NotBlank(message = "El email es requerido")
        @Email(message = "El email no es válido")
        String email
) {
}