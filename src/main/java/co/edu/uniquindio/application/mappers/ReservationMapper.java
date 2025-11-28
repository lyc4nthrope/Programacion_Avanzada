package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.create.CreateReservationDTO;
import co.edu.uniquindio.application.dto.ReservationDTO;
import co.edu.uniquindio.application.models.entitys.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReservationMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "accommodation", ignore = true)
    @Mapping(target = "guest", ignore = true)
    @Mapping(target = "payment", ignore = true)
    @Mapping(target = "review", ignore = true)
    Reservation toEntity(CreateReservationDTO createReservationDTO);

    @Mapping(source = "accommodation.title", target = "accommodationTitle")
    @Mapping(source = "accommodation.id", target = "accommodationId")
    @Mapping(source = "guest.name", target = "guestName")
    @Mapping(source = "guest.id", target = "guestId")
    ReservationDTO toReservationDTO(Reservation reservation);
}