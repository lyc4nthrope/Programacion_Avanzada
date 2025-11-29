package co.edu.uniquindio.application.dto.create;

import jakarta.validation.constraints.NotBlank;

public record CreateMessageDTO(
        @NotBlank(message = "El ID del chat es requerido")
        String chatId,
        
        @NotBlank(message = "El ID del remitente es requerido")
        String senderId,
        
        @NotBlank(message = "El ID del destinatario es requerido")
        String recipientId,
        
        @NotBlank(message = "El contenido del mensaje no puede estar vacío")
        @org.hibernate.validator.constraints.Length(min = 1, max = 1000, message = "El mensaje debe tener entre 1 y 1000 caracteres")
        String content
) {
}