package co.edu.uniquindio.application.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateChatDTO(
        @NotNull(message = "La lista de participantes es requerida")
        List<String> participantIds
) {
}