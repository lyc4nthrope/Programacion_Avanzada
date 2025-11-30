package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.create.CreateAccommodationDTO;
import co.edu.uniquindio.application.dto.AccommodationDTO;
import co.edu.uniquindio.application.models.entitys.Accommodation;
import co.edu.uniquindio.application.models.enums.AccommodationStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccommodationMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "averageRating", constant = "0.0")
    @Mapping(target = "ratingCount", constant = "0")
    @Mapping(target = "status", expression = "java(co.edu.uniquindio.application.models.enums.AccommodationStatus.ACTIVE)")  // ✅ NUEVO
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "host", ignore = true)
    Accommodation toEntity(CreateAccommodationDTO accommodationDTO);

    @Mapping(source = "host.id", target = "hostId")
    AccommodationDTO toAccommodationDTO(Accommodation accommodation);
}