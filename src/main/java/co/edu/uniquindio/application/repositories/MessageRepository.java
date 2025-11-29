package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.models.entitys.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

    // Buscar mensajes de un chat
    List<Message> findByChatId(String chatId);

    // Buscar mensajes de un chat ordenados por fecha
    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.sentAt ASC")
    List<Message> findByChatIdOrderByDate(@Param("chatId") String chatId);

    // Buscar mensajes no leídos de un usuario
    List<Message> findByRecipientIdAndIsReadFalse(String recipientId);

    // Buscar mensajes no leídos de un chat específico
    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId AND m.isRead = false")
    List<Message> findUnreadMessagesByChat(@Param("chatId") String chatId);

    // Buscar mensajes entre dos usuarios
    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId AND " +
           "((m.sender.id = :userId1 AND m.recipient.id = :userId2) OR " +
           "(m.sender.id = :userId2 AND m.recipient.id = :userId1)) " +
           "ORDER BY m.sentAt ASC")
    List<Message> findMessagesBetweenUsersInChat(@Param("chatId") String chatId, 
                                                  @Param("userId1") String userId1, 
                                                  @Param("userId2") String userId2);

    // Buscar mensajes enviados por un usuario
    List<Message> findBySenderId(String senderId);

    // Contar mensajes de un chat
    Long countByChatId(String chatId);

    // Contar mensajes no leídos de un usuario
    Long countByRecipientIdAndIsReadFalse(String recipientId);
}