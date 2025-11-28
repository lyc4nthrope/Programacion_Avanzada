package co.edu.uniquindio.application.dto.edit;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

public record EditReviewDTO(
        @NotBlank(message = "El comentario no puede estar vacío")
        @Length(min = 10, max = 2000, message = "El comentario debe tener entre 10 y 2000 caracteres")
        String comment,

        @NotNull(message = "La calificación es requerida")
        @Min(value = 1, message = "La calificación mínima es 1")
        @Max(value = 5, message = "La calificación máxima es 5")
        Integer rating
) {
}