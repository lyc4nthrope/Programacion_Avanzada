package co.edu.uniquindio.application.dto;

import jakarta.validation.constraints.NotBlank;

public record ValidatePasswordResetCodeDTO(
        @NotBlank(message = "El código es requerido")
        String code,
        
        @NotBlank(message = "La nueva contraseña es requerida")
        @org.hibernate.validator.constraints.Length(min = 7, max = 20)
        String newPassword
) {
}