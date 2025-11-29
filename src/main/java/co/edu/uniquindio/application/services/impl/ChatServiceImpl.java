package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.create.CreateChatDTO;
import co.edu.uniquindio.application.dto.ChatDTO;
import co.edu.uniquindio.application.exceptions.InvalidOperationException;
import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.mappers.ChatMapper;
import co.edu.uniquindio.application.models.entitys.Chat;
import co.edu.uniquindio.application.models.entitys.User;
import co.edu.uniquindio.application.repositories.ChatRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;
    private final UserRepository userRepository;

    @Override
    public void create(CreateChatDTO chatDTO) throws Exception {
        // Validar que hay al menos 2 participantes
        if (chatDTO.participantIds() == null || chatDTO.participantIds().size() < 2) {
            throw new InvalidOperationException("Un chat debe tener al menos 2 participantes.");
        }

        // Validar que no hay participantes duplicados
        if (chatDTO.participantIds().size() != chatDTO.participantIds().stream().distinct().count()) {
            throw new InvalidOperationException("No puede haber participantes duplicados en un chat.");
        }

        // Validar que todos los usuarios existen
        List<User> users = new ArrayList<>();
        for (String userId : chatDTO.participantIds()) {
            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                throw new NotFoundException("El usuario con ID '" + userId + "' no fue encontrado.");
            }
            users.add(user.get());
        }

        // Crear el chat
        Chat newChat = chatMapper.toEntity(chatDTO);
        newChat.setUsers(users);

        // Guardar el chat
        chatRepository.save(newChat);
    }

    @Override
    public ChatDTO get(String id) throws Exception {
        Optional<Chat> chatOptional = chatRepository.findById(id);

        if (chatOptional.isEmpty()) {
            throw new NotFoundException("El chat con ID '" + id + "' no fue encontrado.");
        }

        return chatMapper.toChatDTO(chatOptional.get());
    }

    @Override
    public void delete(String id) throws Exception {
        Optional<Chat> chatOptional = chatRepository.findById(id);

        if (chatOptional.isEmpty()) {
            throw new NotFoundException("El chat con ID '" + id + "' no fue encontrado.");
        }

        chatRepository.deleteById(id);
    }

    @Override
    public List<ChatDTO> listAll() {
        return chatRepository.findAll()
                .stream()
                .map(chatMapper::toChatDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatDTO> listActive() {
        return chatRepository.findByActiveTrue()
                .stream()
                .map(chatMapper::toChatDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatDTO> listByUser(String userId) throws Exception {
        // Validar que el usuario existe
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("El usuario con ID '" + userId + "' no fue encontrado.");
        }

        return chatRepository.findByUserId(userId)
                .stream()
                .map(chatMapper::toChatDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatDTO> listActiveChatsForUser(String userId) throws Exception {
        // Validar que el usuario existe
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("El usuario con ID '" + userId + "' no fue encontrado.");
        }

        return chatRepository.findActiveChatsForUser(userId)
                .stream()
                .map(chatMapper::toChatDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ChatDTO getOrCreateChatBetweenUsers(String userId1, String userId2) throws Exception {
        // Validar que ambos usuarios existen
        if (userRepository.findById(userId1).isEmpty()) {
            throw new NotFoundException("El usuario con ID '" + userId1 + "' no fue encontrado.");
        }

        if (userRepository.findById(userId2).isEmpty()) {
            throw new NotFoundException("El usuario con ID '" + userId2 + "' no fue encontrado.");
        }

        // Validar que no sean el mismo usuario
        if (userId1.equals(userId2)) {
            throw new InvalidOperationException("No puedes crear un chat contigo mismo.");
        }

        // Buscar chat existente
        Optional<Chat> existingChat = chatRepository.findChatBetweenTwoUsers(userId1, userId2);
        if (existingChat.isPresent()) {
            return chatMapper.toChatDTO(existingChat.get());
        }

        // Crear nuevo chat
        List<String> participantIds = List.of(userId1, userId2);
        CreateChatDTO createChatDTO = new CreateChatDTO(participantIds);
        create(createChatDTO);

        // Buscar y retornar el chat creado
        Optional<Chat> newChat = chatRepository.findChatBetweenTwoUsers(userId1, userId2);
        return chatMapper.toChatDTO(newChat.get());
    }

    @Override
    public void activateChat(String id) throws Exception {
        Optional<Chat> chatOptional = chatRepository.findById(id);

        if (chatOptional.isEmpty()) {
            throw new NotFoundException("El chat con ID '" + id + "' no fue encontrado.");
        }

        Chat chat = chatOptional.get();
        chat.setActive(true);
        chatRepository.save(chat);
    }

    @Override
    public void deactivateChat(String id) throws Exception {
        Optional<Chat> chatOptional = chatRepository.findById(id);

        if (chatOptional.isEmpty()) {
            throw new NotFoundException("El chat con ID '" + id + "' no fue encontrado.");
        }

        Chat chat = chatOptional.get();
        chat.setActive(false);
        chatRepository.save(chat);
    }

    @Override
    public Long countActiveChats() {
        return chatRepository.countByActiveTrue();
    }
}