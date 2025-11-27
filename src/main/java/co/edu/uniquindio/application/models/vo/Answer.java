package co.edu.uniquindio.application.models.vo;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Answer {

    private String texto;                // Texto de la respuesta
    private LocalDateTime fechaRespuesta; // Fecha de la respuesta
}