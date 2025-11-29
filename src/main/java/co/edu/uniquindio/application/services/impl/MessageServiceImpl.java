package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.create.CreateMessageDTO;
import co.edu.uniquindio.application.dto.MessageDTO;
import co.edu.uniquindio.application.exceptions.InvalidOperationException;
import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.mappers.MessageMapper;
import co.edu.uniquindio.application.models.entitys.Chat;
import co.edu.uniquindio.application.models.entitys.Message;
import co.edu.uniquindio.application.models.entitys.User;
import co.edu.uniquindio.application.repositories.ChatRepository;
import co.edu.uniquindio.application.repositories.MessageRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    @Override
    public void create(CreateMessageDTO messageDTO) throws Exception {
        // Validar que el chat existe
        Optional<Chat> chat = chatRepository.findById(messageDTO.chatId());
        if (chat.isEmpty()) {
            throw new NotFoundException("El chat con ID '" + messageDTO.chatId() + "' no fue encontrado.");
        }

        // Validar que el remitente existe
        Optional<User> sender = userRepository.findById(messageDTO.senderId());
        if (sender.isEmpty()) {
            throw new NotFoundException("El remitente con ID '" + messageDTO.senderId() + "' no fue encontrado.");
        }

        // Validar que el destinatario existe
        Optional<User> recipient = userRepository.findById(messageDTO.recipientId());
        if (recipient.isEmpty()) {
            throw new NotFoundException("El destinatario con ID '" + messageDTO.recipientId() + "' no fue encontrado.");
        }

        // Validar que el remitente y destinatario son diferentes
        if (messageDTO.senderId().equals(messageDTO.recipientId())) {
            throw new InvalidOperationException("No puedes enviar un mensaje a ti mismo.");
        }

        // Validar que ambos usuarios están en el chat
        boolean senderInChat = chat.get().getUsers().stream()
                .anyMatch(u -> u.getId().equals(messageDTO.senderId()));
        boolean recipientInChat = chat.get().getUsers().stream()
                .anyMatch(u -> u.getId().equals(messageDTO.recipientId()));

        if (!senderInChat || !recipientInChat) {
            throw new InvalidOperationException("Ambos usuarios deben estar en el chat para enviar mensajes.");
        }

        // Crear el mensaje
        Message newMessage = messageMapper.toEntity(messageDTO);
        newMessage.setChat(chat.get());
        newMessage.setSender(sender.get());
        newMessage.setRecipient(recipient.get());

        // Guardar el mensaje
        messageRepository.save(newMessage);
    }

    @Override
    public MessageDTO get(String id) throws Exception {
        Optional<Message> messageOptional = messageRepository.findById(id);

        if (messageOptional.isEmpty()) {
            throw new NotFoundException("El mensaje con ID '" + id + "' no fue encontrado.");
        }

        return messageMapper.toMessageDTO(messageOptional.get());
    }

    @Override
    public void delete(String id) throws Exception {
        Optional<Message> messageOptional = messageRepository.findById(id);

        if (messageOptional.isEmpty()) {
            throw new NotFoundException("El mensaje con ID '" + id + "' no fue encontrado.");
        }

        messageRepository.deleteById(id);
    }

    @Override
    public List<MessageDTO> listAll() {
        return messageRepository.findAll()
                .stream()
                .map(messageMapper::toMessageDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> listByChat(String chatId) throws Exception {
        // Validar que el chat existe
        if (chatRepository.findById(chatId).isEmpty()) {
            throw new NotFoundException("El chat con ID '" + chatId + "' no fue encontrado.");
        }

        return messageRepository.findByChatId(chatId)
                .stream()
                .map(messageMapper::toMessageDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> listByChatOrderByDate(String chatId) throws Exception {
        // Validar que el chat existe
        if (chatRepository.findById(chatId).isEmpty()) {
            throw new NotFoundException("El chat con ID '" + chatId + "' no fue encontrado.");
        }

        return messageRepository.findByChatIdOrderByDate(chatId)
                .stream()
                .map(messageMapper::toMessageDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> listUnreadMessages(String userId) throws Exception {
        // Validar que el usuario existe
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("El usuario con ID '" + userId + "' no fue encontrado.");
        }

        return messageRepository.findByRecipientIdAndIsReadFalse(userId)
                .stream()
                .map(messageMapper::toMessageDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> listUnreadMessagesByChat(String chatId) throws Exception {
        // Validar que el chat existe
        if (chatRepository.findById(chatId).isEmpty()) {
            throw new NotFoundException("El chat con ID '" + chatId + "' no fue encontrado.");
        }

        return messageRepository.findUnreadMessagesByChat(chatId)
                .stream()
                .map(messageMapper::toMessageDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> listMessagesBetweenUsers(String chatId, String userId1, String userId2) throws Exception {
        // Validar que el chat existe
        if (chatRepository.findById(chatId).isEmpty()) {
            throw new NotFoundException("El chat con ID '" + chatId + "' no fue encontrado.");
        }

        // Validar que ambos usuarios existen
        if (userRepository.findById(userId1).isEmpty()) {
            throw new NotFoundException("El usuario con ID '" + userId1 + "' no fue encontrado.");
        }

        if (userRepository.findById(userId2).isEmpty()) {
            throw new NotFoundException("El usuario con ID '" + userId2 + "' no fue encontrado.");
        }

        return messageRepository.findMessagesBetweenUsersInChat(chatId, userId1, userId2)
                .stream()
                .map(messageMapper::toMessageDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(String messageId) throws Exception {
        Optional<Message> messageOptional = messageRepository.findById(messageId);

        if (messageOptional.isEmpty()) {
            throw new NotFoundException("El mensaje con ID '" + messageId + "' no fue encontrado.");
        }

        Message message = messageOptional.get();
        message.setIsRead(true);
        messageRepository.save(message);
    }

    @Override
    public void markAllChatMessagesAsRead(String chatId) throws Exception {
        // Validar que el chat existe
        if (chatRepository.findById(chatId).isEmpty()) {
            throw new NotFoundException("El chat con ID '" + chatId + "' no fue encontrado.");
        }

        List<Message> unreadMessages = messageRepository.findUnreadMessagesByChat(chatId);
        for (Message message : unreadMessages) {
            message.setIsRead(true);
            messageRepository.save(message);
        }
    }

    @Override
    public Long countUnreadMessages(String userId) throws Exception {
        // Validar que el usuario existe
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("El usuario con ID '" + userId + "' no fue encontrado.");
        }

        return messageRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    @Override
    public MessageDTO getLastMessage(String chatId) throws Exception {
        // Validar que el chat existe
        if (chatRepository.findById(chatId).isEmpty()) {
            throw new NotFoundException("El chat con ID '" + chatId + "' no fue encontrado.");
        }

        List<Message> messages = messageRepository.findByChatIdOrderByDate(chatId);
        if (messages.isEmpty()) {
            throw new NotFoundException("No hay mensajes en este chat.");
        }

        Message lastMessage = messages.get(messages.size() - 1);
        return messageMapper.toMessageDTO(lastMessage);
    }
}