package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.create.CreateMessageDTO;
import co.edu.uniquindio.application.dto.MessageDTO;

import java.util.List;

public interface MessageService {

    // Crear un nuevo mensaje
    void create(CreateMessageDTO messageDTO) throws Exception;

    // Obtener un mensaje por ID
    MessageDTO get(String id) throws Exception;

    // Eliminar un mensaje
    void delete(String id) throws Exception;

    // Listar todos los mensajes
    List<MessageDTO> listAll();

    // Listar mensajes de un chat
    List<MessageDTO> listByChat(String chatId) throws Exception;

    // Listar mensajes de un chat ordenados por fecha
    List<MessageDTO> listByChatOrderByDate(String chatId) throws Exception;

    // Listar mensajes no leídos de un usuario
    List<MessageDTO> listUnreadMessages(String userId) throws Exception;

    // Listar mensajes no leídos de un chat
    List<MessageDTO> listUnreadMessagesByChat(String chatId) throws Exception;

    // Listar mensajes entre dos usuarios en un chat
    List<MessageDTO> listMessagesBetweenUsers(String chatId, String userId1, String userId2) throws Exception;

    // Marcar mensaje como leído
    void markAsRead(String messageId) throws Exception;

    // Marcar todos los mensajes del chat como leídos
    void markAllChatMessagesAsRead(String chatId) throws Exception;

    // Obtener conteo de mensajes no leídos
    Long countUnreadMessages(String userId) throws Exception;

    // Obtener último mensaje de un chat
    MessageDTO getLastMessage(String chatId) throws Exception;
}