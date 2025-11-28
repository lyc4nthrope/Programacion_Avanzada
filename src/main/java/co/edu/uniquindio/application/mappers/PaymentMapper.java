package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.create.CreatePaymentDTO;
import co.edu.uniquindio.application.dto.PaymentDTO;
import co.edu.uniquindio.application.models.entitys.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "reservation", ignore = true)
    Payment toEntity(CreatePaymentDTO createPaymentDTO);

    @Mapping(source = "reservation.id", target = "reservationId")
    PaymentDTO toPaymentDTO(Payment payment);
}