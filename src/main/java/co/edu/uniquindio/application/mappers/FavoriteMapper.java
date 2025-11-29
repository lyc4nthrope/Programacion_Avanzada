package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.create.CreateFavoriteDTO;
import co.edu.uniquindio.application.dto.FavoriteDTO;
import co.edu.uniquindio.application.models.entitys.Favorite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FavoriteMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "accommodation", ignore = true)
    Favorite toEntity(CreateFavoriteDTO createFavoriteDTO);

    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "accommodation.title", target = "accommodationTitle")
    @Mapping(source = "accommodation.id", target = "accommodationId")
    @Mapping(source = "accommodation.city", target = "accommodationCity")
    @Mapping(source = "accommodation.pricePerNight", target = "accommodationPrice")
    @Mapping(source = "accommodation.averageRating", target = "accommodationRating")
    FavoriteDTO toFavoriteDTO(Favorite favorite);
}