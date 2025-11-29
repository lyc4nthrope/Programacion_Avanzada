package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.create.CreateHostProfileDTO;
import co.edu.uniquindio.application.dto.HostProfileDTO;
import co.edu.uniquindio.application.models.entitys.HostProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HostProfileMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "user", ignore = true)
    HostProfile toEntity(CreateHostProfileDTO createHostProfileDTO);

    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "user.id", target = "userId")
    HostProfileDTO toHostProfileDTO(HostProfile hostProfile);
}