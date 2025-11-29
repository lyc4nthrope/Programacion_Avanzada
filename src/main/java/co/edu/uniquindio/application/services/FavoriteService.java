package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.create.CreateFavoriteDTO;
import co.edu.uniquindio.application.dto.FavoriteDTO;

import java.util.List;

public interface FavoriteService {

    // Agregar un alojamiento a favoritos
    void addFavorite(CreateFavoriteDTO favoriteDTO) throws Exception;

    // Obtener un favorito por ID
    FavoriteDTO get(String id) throws Exception;

    // Eliminar un favorito
    void removeFavorite(String id) throws Exception;

    // Eliminar favorito por usuario y alojamiento
    void removeFavoriteByUserAndAccommodation(String userId, String accommodationId) throws Exception;

    // Listar todos los favoritos
    List<FavoriteDTO> listAll();

    // Listar favoritos de un usuario
    List<FavoriteDTO> listByUser(String userId) throws Exception;

    // Listar favoritos de un alojamiento
    List<FavoriteDTO> listByAccommodation(String accommodationId) throws Exception;

    // Verificar si un alojamiento es favorito de un usuario
    boolean isFavorite(String userId, String accommodationId) throws Exception;

    // Contar favoritos de un usuario
    Long countFavoritesByUser(String userId) throws Exception;

    // Contar favoritos de un alojamiento
    Long countFavoritesByAccommodation(String accommodationId) throws Exception;
}