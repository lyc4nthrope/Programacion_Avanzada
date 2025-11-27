package co.edu.uniquindio.application.models.vo;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.time.LocalDateTime;

@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Answer {
    private String text;              // Texto de respuesta
    private LocalDateTime responseDate; // Fecha de respuesta
}