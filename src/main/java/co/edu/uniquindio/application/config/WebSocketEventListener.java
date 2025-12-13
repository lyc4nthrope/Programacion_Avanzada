package co.edu.uniquindio.application.config;

import co.edu.uniquindio.application.dto.websocket.ChatMessageWS;
import co.edu.uniquindio.application.models.enums.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        log.info("Usuario conectado - Session ID: {}", sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        
        String sessionId = headerAccessor.getSessionId();
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        String userName = (String) headerAccessor.getSessionAttributes().get("userName");
        String chatId = (String) headerAccessor.getSessionAttributes().get("chatId");
        
        log.info("Usuario desconectado - User: {}, Session: {}", userName, sessionId);
        
        // Si el usuario estaba en un chat, notificar su salida
        if (chatId != null && userId != null) {
            ChatMessageWS leaveMessage = ChatMessageWS.builder()
                    .type(MessageType.LEAVE)
                    .chatId(chatId)
                    .senderId(userId)
                    .senderName(userName)
                    .timestamp(java.time.LocalDateTime.now())
                    .build();
            
            messagingTemplate.convertAndSend(
                    "/topic/chat/" + chatId,
                    leaveMessage
            );
            
            log.info("Notificación de salida enviada al chat {}", chatId);
        }
    }
}