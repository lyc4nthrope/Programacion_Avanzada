package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.create.CreateAccommodationDTO;
import co.edu.uniquindio.application.dto.AccommodationDTO;
import co.edu.uniquindio.application.models.entitys.Accommodation;
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
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "host", ignore = true)  // ✅ Agregar esta línea
    Accommodation toEntity(CreateAccommodationDTO accommodationDTO);

    @Mapping(source = "host.id", target = "hostId")  // ✅ Mapear el host
    AccommodationDTO toAccommodationDTO(Accommodation accommodation);
}