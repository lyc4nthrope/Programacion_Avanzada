package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.create.CreateMessageDTO;
import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.dto.MessageDTO;
import co.edu.uniquindio.application.services.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody CreateMessageDTO messageDTO) throws Exception {
        messageService.create(messageDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(false, "El mensaje ha sido enviado exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<MessageDTO>> get(@PathVariable String id) throws Exception {
        MessageDTO messageDTO = messageService.get(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, messageDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> delete(@PathVariable String id) throws Exception {
        messageService.delete(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El mensaje ha sido eliminado"));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<MessageDTO>>> listAll() {
        List<MessageDTO> list = messageService.listAll();
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<ResponseDTO<List<MessageDTO>>> listByChat(@PathVariable String chatId) throws Exception {
        List<MessageDTO> list = messageService.listByChat(chatId);
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @GetMapping("/chat/{chatId}/ordered")
    public ResponseEntity<ResponseDTO<List<MessageDTO>>> listByChatOrderByDate(@PathVariable String chatId) throws Exception {
        List<MessageDTO> list = messageService.listByChatOrderByDate(chatId);
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<ResponseDTO<List<MessageDTO>>> listUnreadMessages(@PathVariable String userId) throws Exception {
        List<MessageDTO> list = messageService.listUnreadMessages(userId);
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @GetMapping("/chat/{chatId}/unread")
    public ResponseEntity<ResponseDTO<List<MessageDTO>>> listUnreadMessagesByChat(@PathVariable String chatId) throws Exception {
        List<MessageDTO> list = messageService.listUnreadMessagesByChat(chatId);
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @GetMapping("/chat/{chatId}/between/{userId1}/{userId2}")
    public ResponseEntity<ResponseDTO<List<MessageDTO>>> listMessagesBetweenUsers(
            @PathVariable String chatId,
            @PathVariable String userId1,
            @PathVariable String userId2) throws Exception {
        List<MessageDTO> list = messageService.listMessagesBetweenUsers(chatId, userId1, userId2);
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ResponseDTO<String>> markAsRead(@PathVariable String id) throws Exception {
        messageService.markAsRead(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El mensaje ha sido marcado como leído"));
    }

    @PutMapping("/chat/{chatId}/read-all")
    public ResponseEntity<ResponseDTO<String>> markAllChatMessagesAsRead(@PathVariable String chatId) throws Exception {
        messageService.markAllChatMessagesAsRead(chatId);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Todos los mensajes del chat han sido marcados como leídos"));
    }

    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<ResponseDTO<Long>> countUnreadMessages(@PathVariable String userId) throws Exception {
        Long count = messageService.countUnreadMessages(userId);
        return ResponseEntity.ok(new ResponseDTO<>(false, count));
    }

    @GetMapping("/chat/{chatId}/last")
    public ResponseEntity<ResponseDTO<MessageDTO>> getLastMessage(@PathVariable String chatId) throws Exception {
        MessageDTO messageDTO = messageService.getLastMessage(chatId);
        return ResponseEntity.ok(new ResponseDTO<>(false, messageDTO));
    }

    @GetMapping("/stats/user/{userId}")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getStats(@PathVariable String userId) throws Exception {
        Map<String, Object> stats = new HashMap<>();
        Long unreadCount = messageService.countUnreadMessages(userId);
        
        stats.put("user_id", userId);
        stats.put("unread_message_count", unreadCount);
        
        return ResponseEntity.ok(new ResponseDTO<>(false, stats));
    }
}