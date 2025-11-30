package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.create.CreateReviewDTO;
import co.edu.uniquindio.application.dto.ReviewDTO;
import co.edu.uniquindio.application.models.entitys.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReviewMapper {

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accommodation", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "reservation", ignore = true)  // ✅ Se establece en el servicio si existe
    @Mapping(target = "answer", ignore = true)
    Review toEntity(CreateReviewDTO createReviewDTO);

    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "accommodation.id", target = "accommodationId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "reservation.id", target = "reservationId")  // ✅ NUEVO
    ReviewDTO toReviewDTO(Review review);
}