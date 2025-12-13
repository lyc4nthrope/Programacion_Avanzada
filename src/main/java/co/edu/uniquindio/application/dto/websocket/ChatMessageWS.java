package co.edu.uniquindio.application.dto.websocket;

import co.edu.uniquindio.application.models.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageWS {
    
    // Tipo de mensaje
    private MessageType type;
    
    // IDs
    private String messageId;
    private String chatId;
    private String senderId;
    private String senderName;
    private String recipientId;
    private String recipientName;
    
    // Contenido
    private String content;
    
    // Metadata
    private LocalDateTime timestamp;
    private Boolean isRead;

    public static ChatMessageWS createChatMessage(
            String messageId,
            String chatId,
            String senderId,
            String senderName,
            String recipientId,
            String content) {
        return ChatMessageWS.builder()
                .type(MessageType.CHAT)
                .messageId(messageId)
                .chatId(chatId)
                .senderId(senderId)
                .senderName(senderName)
                .recipientId(recipientId)
                .content(content)
                .timestamp(LocalDateTime.now())
                .isRead(false)
                .build();
    }

    public static ChatMessageWS createTypingMessage(
            String chatId,
            String userId,
            String userName) {
        return ChatMessageWS.builder()
                .type(MessageType.TYPING)
                .chatId(chatId)
                .senderId(userId)
                .senderName(userName)
                .timestamp(LocalDateTime.now())
                .build();
    }
}