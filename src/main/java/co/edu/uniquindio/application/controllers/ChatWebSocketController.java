package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.MessageDTO;
import co.edu.uniquindio.application.dto.create.CreateMessageDTO;
import co.edu.uniquindio.application.dto.websocket.ChatMessageWS;
import co.edu.uniquindio.application.models.enums.MessageType;
import co.edu.uniquindio.application.services.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageWS message) {
        try {
            log.info("Mensaje recibido - Chat: {}, De: {}, Para: {}",
                    message.getChatId(), 
                    message.getSenderName(), 
                    message.getRecipientName());
            
            // 1. Guardar mensaje en base de datos
            CreateMessageDTO createDTO = new CreateMessageDTO(
                    message.getChatId(),
                    message.getSenderId(),
                    message.getRecipientId(),
                    message.getContent()
            );
            
            messageService.create(createDTO);
            
            // 2. Enviar a todos los suscriptores del chat
            messagingTemplate.convertAndSend(
                    "/topic/chat/" + message.getChatId(),
                    message
            );
            
            // 3. Enviar notificación personal al destinatario
            messagingTemplate.convertAndSendToUser(
                    message.getRecipientId(),
                    "/queue/messages",
                    message
            );
            
            log.info("Mensaje enviado exitosamente");
            
        } catch (Exception e) {
            log.error("Error al enviar mensaje: {}", e.getMessage());
            
            // Enviar error al remitente
            ChatMessageWS errorMessage = ChatMessageWS.builder()
                    .type(MessageType.CHAT)
                    .content("Error al enviar mensaje: " + e.getMessage())
                    .timestamp(java.time.LocalDateTime.now())
                    .build();
            
            messagingTemplate.convertAndSendToUser(
                    message.getSenderId(),
                    "/queue/errors",
                    errorMessage
            );
        }
    }

    @MessageMapping("/chat.typing/{chatId}")
    @SendTo("/topic/chat/{chatId}/typing")
    public ChatMessageWS userTyping(
            @DestinationVariable String chatId,
            @Payload ChatMessageWS message) {
        
        log.info("{} está escribiendo en chat {}", message.getSenderName(), chatId);
        return message;
    }

    @MessageMapping("/chat.stopTyping/{chatId}")
    @SendTo("/topic/chat/{chatId}/typing")
    public ChatMessageWS userStopTyping(
            @DestinationVariable String chatId,
            @Payload ChatMessageWS message) {
        
        log.info("{} dejó de escribir en chat {}", message.getSenderName(), chatId);
        message.setType(MessageType.STOP_TYPING);
        return message;
    }

    @MessageMapping("/chat.markRead")
    public void markMessageAsRead(@Payload ChatMessageWS message) {
        try {
            log.info("Marcando mensaje como leído: {}", message.getMessageId());
            
            // Actualizar en base de datos
            messageService.markAsRead(message.getMessageId());
            
            // Notificar al remitente que su mensaje fue leído
            ChatMessageWS readReceipt = ChatMessageWS.builder()
                    .type(MessageType.READ_RECEIPT)
                    .messageId(message.getMessageId())
                    .chatId(message.getChatId())
                    .recipientId(message.getSenderId())
                    .timestamp(java.time.LocalDateTime.now())
                    .build();
            
            messagingTemplate.convertAndSendToUser(
                    message.getSenderId(),
                    "/queue/receipts",
                    readReceipt
            );
            
        } catch (Exception e) {
            log.error("Error al marcar como leído: {}", e.getMessage());
        }
    }

    @MessageMapping("/chat.join/{chatId}")
    @SendTo("/topic/chat/{chatId}")
    public ChatMessageWS userJoinChat(
            @DestinationVariable String chatId,
            @Payload ChatMessageWS message) {
        
        log.info("{} se unió al chat {}", message.getSenderName(), chatId);
        message.setType(MessageType.JOIN);
        return message;
    }

    @MessageMapping("/chat.leave/{chatId}")
    @SendTo("/topic/chat/{chatId}")
    public ChatMessageWS userLeaveChat(
            @DestinationVariable String chatId,
            @Payload ChatMessageWS message) {
        
        log.info("{} salió del chat {}", message.getSenderName(), chatId);
        message.setType(MessageType.LEAVE);
        return message;
    }
}