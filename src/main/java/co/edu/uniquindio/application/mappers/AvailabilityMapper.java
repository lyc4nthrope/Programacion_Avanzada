package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.create.CreateAvailabilityDTO;
import co.edu.uniquindio.application.dto.AvailabilityDTO;
import co.edu.uniquindio.application.models.entitys.AvailabilityCalendar;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AvailabilityMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "accommodation", ignore = true)
    AvailabilityCalendar toEntity(CreateAvailabilityDTO createAvailabilityDTO);

    @Mapping(source = "accommodation.title", target = "accommodationTitle")
    @Mapping(source = "accommodation.id", target = "accommodationId")
    AvailabilityDTO toAvailabilityDTO(AvailabilityCalendar availabilityCalendar);
}