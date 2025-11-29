package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.create.CreateMessageDTO;
import co.edu.uniquindio.application.dto.MessageDTO;
import co.edu.uniquindio.application.models.entitys.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MessageMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "sentAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "isRead", constant = "false")
    @Mapping(target = "chat", ignore = true)
    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "recipient", ignore = true)
    Message toEntity(CreateMessageDTO createMessageDTO);

    @Mapping(source = "sender.name", target = "senderName")
    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "recipient.name", target = "recipientName")
    @Mapping(source = "recipient.id", target = "recipientId")
    @Mapping(source = "chat.id", target = "chatId")
    MessageDTO toMessageDTO(Message message);
}