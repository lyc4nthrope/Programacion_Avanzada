package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.AccommodationPhotoDTO;
import co.edu.uniquindio.application.dto.create.CreateAccommodationPhotoDTO;
import co.edu.uniquindio.application.models.entitys.AccommodationPhoto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccommodationPhotoMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "accommodation", ignore = true)
    AccommodationPhoto toEntity(CreateAccommodationPhotoDTO dto);

    @Mapping(source = "accommodation.id", target = "accommodationId")
    AccommodationPhotoDTO toDTO(AccommodationPhoto entity);
}