package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.create.CreateChatDTO;
import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.dto.ChatDTO;
import co.edu.uniquindio.application.services.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody CreateChatDTO chatDTO) throws Exception {
        chatService.create(chatDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(false, "El chat ha sido creado exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<ChatDTO>> get(@PathVariable String id) throws Exception {
        ChatDTO chatDTO = chatService.get(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, chatDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> delete(@PathVariable String id) throws Exception {
        chatService.delete(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El chat ha sido eliminado"));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<ChatDTO>>> listAll() {
        List<ChatDTO> list = chatService.listAll();
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @GetMapping("/active")
    public ResponseEntity<ResponseDTO<List<ChatDTO>>> listActive() {
        List<ChatDTO> list = chatService.listActive();
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseDTO<List<ChatDTO>>> listByUser(@PathVariable String userId) throws Exception {
        List<ChatDTO> list = chatService.listByUser(userId);
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<ResponseDTO<List<ChatDTO>>> listActiveChatsForUser(@PathVariable String userId) throws Exception {
        List<ChatDTO> list = chatService.listActiveChatsForUser(userId);
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PostMapping("/between/{userId1}/{userId2}")
    public ResponseEntity<ResponseDTO<ChatDTO>> getOrCreateChatBetweenUsers(
            @PathVariable String userId1,
            @PathVariable String userId2) throws Exception {
        ChatDTO chatDTO = chatService.getOrCreateChatBetweenUsers(userId1, userId2);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(false, chatDTO));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<ResponseDTO<String>> activateChat(@PathVariable String id) throws Exception {
        chatService.activateChat(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El chat ha sido activado"));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ResponseDTO<String>> deactivateChat(@PathVariable String id) throws Exception {
        chatService.deactivateChat(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El chat ha sido desactivado"));
    }

    @GetMapping("/stats")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        List<ChatDTO> allChats = chatService.listAll();
        List<ChatDTO> activeChats = chatService.listActive();
        
        stats.put("total_chats", allChats.size());
        stats.put("active_chats", activeChats.size());
        stats.put("inactive_chats", allChats.size() - activeChats.size());
        
        return ResponseEntity.ok(new ResponseDTO<>(false, stats));
    }
}