package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.create.CreateReviewDTO;
import co.edu.uniquindio.application.dto.edit.EditReviewDTO;
import co.edu.uniquindio.application.dto.ReviewDTO;

import java.util.List;

public interface ReviewService {

    // Crear una nueva reseña
    void create(CreateReviewDTO reviewDTO) throws Exception;

    // Obtener una reseña por ID
    ReviewDTO get(Long id) throws Exception;

    // Eliminar una reseña
    void delete(Long id) throws Exception;

    // Listar todas las reseñas
    List<ReviewDTO> listAll();

    // Listar reseñas de un alojamiento
    List<ReviewDTO> listByAccommodation(String accommodationId) throws Exception;

    // Listar reseñas de un usuario
    List<ReviewDTO> listByUser(String userId) throws Exception;

    // Editar una reseña
    void edit(Long id, EditReviewDTO reviewDTO) throws Exception;

    // Obtener calificación promedio de un alojamiento
    Double getAverageRating(String accommodationId) throws Exception;
}