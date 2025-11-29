package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.create.CreateChatDTO;
import co.edu.uniquindio.application.dto.ChatDTO;
import co.edu.uniquindio.application.models.entitys.Chat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "messages", ignore = true)
    Chat toEntity(CreateChatDTO createChatDTO);

    @Mapping(source = "users", target = "participantIds", qualifiedByName = "mapUserIds")
    @Mapping(source = "users", target = "participantNames", qualifiedByName = "mapUserNames")
    @Mapping(source = "messages", target = "messageCount", qualifiedByName = "countMessages")
    @Mapping(source = "messages", target = "lastMessageAt", qualifiedByName = "getLastMessageTime")
    ChatDTO toChatDTO(Chat chat);

    @org.mapstruct.Named("mapUserIds")
    default java.util.List<String> mapUserIds(java.util.List<co.edu.uniquindio.application.models.entitys.User> users) {
        if (users == null) return java.util.Collections.emptyList();
        return users.stream()
                .map(co.edu.uniquindio.application.models.entitys.User::getId)
                .toList();
    }

    @org.mapstruct.Named("mapUserNames")
    default java.util.List<String> mapUserNames(java.util.List<co.edu.uniquindio.application.models.entitys.User> users) {
        if (users == null) return java.util.Collections.emptyList();
        return users.stream()
                .map(co.edu.uniquindio.application.models.entitys.User::getName)
                .toList();
    }

    @org.mapstruct.Named("countMessages")
    default Long countMessages(java.util.List<co.edu.uniquindio.application.models.entitys.Message> messages) {
        if (messages == null) return 0L;
        return (long) messages.size();
    }

    @org.mapstruct.Named("getLastMessageTime")
    default java.time.LocalDateTime getLastMessageTime(java.util.List<co.edu.uniquindio.application.models.entitys.Message> messages) {
        if (messages == null || messages.isEmpty()) return null;
        return messages.stream()
                .map(co.edu.uniquindio.application.models.entitys.Message::getSentAt)
                .max(java.util.Comparator.naturalOrder())
                .orElse(null);
    }
}