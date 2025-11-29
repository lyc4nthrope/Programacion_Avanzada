package co.edu.uniquindio.application.dto.create;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CreateHostProfileDTO(
        @NotBlank(message = "El ID del usuario es requerido")
        String userId,
        
        @NotBlank(message = "La descripción del anfitrión no puede estar vacía")
        @Length(min = 20, max = 2000, message = "La descripción debe tener entre 20 y 2000 caracteres")
        String aboutMe,
        
        @Length(max = 500, message = "El documento legal no puede exceder 500 caracteres")
        String legalDocument
) {
}