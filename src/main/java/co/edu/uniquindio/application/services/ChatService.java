package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.create.CreateChatDTO;
import co.edu.uniquindio.application.dto.ChatDTO;

import java.util.List;

public interface ChatService {

    // Crear un nuevo chat
    void create(CreateChatDTO chatDTO) throws Exception;

    // Obtener un chat por ID
    ChatDTO get(String id) throws Exception;

    // Eliminar un chat
    void delete(String id) throws Exception;

    // Listar todos los chats
    List<ChatDTO> listAll();

    // Listar chats activos
    List<ChatDTO> listActive();

    // Listar chats de un usuario
    List<ChatDTO> listByUser(String userId) throws Exception;

    // Listar chats activos de un usuario
    List<ChatDTO> listActiveChatsForUser(String userId) throws Exception;

    // Buscar o crear chat entre dos usuarios
    ChatDTO getOrCreateChatBetweenUsers(String userId1, String userId2) throws Exception;

    // Activar un chat
    void activateChat(String id) throws Exception;

    // Desactivar un chat
    void deactivateChat(String id) throws Exception;

    // Obtener cantidad de chats activos
    Long countActiveChats();
}