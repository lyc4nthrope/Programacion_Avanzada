package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.models.entitys.Chat;
import co.edu.uniquindio.application.models.entitys.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, String> {

    // Buscar chats activos
    List<Chat> findByActiveTrue();

    // Buscar chats inactivos
    List<Chat> findByActiveFalse();

    // Buscar chats de un usuario
    @Query("SELECT c FROM Chat c JOIN c.users u WHERE u.id = :userId")
    List<Chat> findByUserId(@Param("userId") String userId);

    // Buscar chats activos de un usuario
    @Query("SELECT c FROM Chat c JOIN c.users u WHERE u.id = :userId AND c.active = true")
    List<Chat> findActiveChatsForUser(@Param("userId") String userId);

    // Buscar chat entre dos usuarios
    @Query("SELECT c FROM Chat c WHERE c.id IN (" +
           "SELECT c2.id FROM Chat c2 JOIN c2.users u1 JOIN c2.users u2 " +
           "WHERE u1.id = :userId1 AND u2.id = :userId2 AND SIZE(c2.users) = 2)")
    Optional<Chat> findChatBetweenTwoUsers(@Param("userId1") String userId1, @Param("userId2") String userId2);

    // Contar chats
    Long countByActiveTrue();
}