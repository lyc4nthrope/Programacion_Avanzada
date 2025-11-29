package co.edu.uniquindio.application.dto;

import java.time.LocalDateTime;

public record MessageDTO(
        String id,
        String chatId,
        String senderId,
        String senderName,
        String recipientId,
        String recipientName,
        String content,
        Boolean isRead,
        LocalDateTime sentAt
) {
}