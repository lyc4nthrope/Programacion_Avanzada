package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.PasswordResetCodeDTO;
import co.edu.uniquindio.application.models.entitys.PasswordResetCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PasswordResetCodeMapper {

    @Mapping(source = "user.id", target = "userId")
    PasswordResetCodeDTO toDTO(PasswordResetCode passwordResetCode);
}