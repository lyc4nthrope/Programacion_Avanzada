package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.models.entitys.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Buscar todas las reviews de un alojamiento
    List<Review> findByAccommodationId(String accommodationId);

    // Buscar todas las reviews de un usuario
    List<Review> findByUserId(String userId);

    // Buscar reviews por calificación
    List<Review> findByRating(Integer rating);

    // Contar reviews por alojamiento
    Long countByAccommodationId(String accommodationId);

    @Query("SELECT r FROM Review r WHERE r.accommodation.id = :accommodationId ORDER BY r.createdAt DESC")
    List<Review> findByAccommodationIdOrderByCreatedAtDesc(@Param("accommodationId") String accommodationId);

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    List<Review> findByUserIdOrderByCreatedAtDesc(@Param("userId") String userId);
}