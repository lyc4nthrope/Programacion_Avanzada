package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.models.entitys.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, String> {

    // Buscar favoritos de un usuario
    List<Favorite> findByUserId(String userId);

    // Buscar favoritos de un alojamiento
    List<Favorite> findByAccommodationId(String accommodationId);

    // Verificar si un alojamiento es favorito de un usuario
    Optional<Favorite> findByUserIdAndAccommodationId(String userId, String accommodationId);

    // Contar favoritos de un usuario
    Long countByUserId(String userId);

    // Contar favoritos de un alojamiento
    Long countByAccommodationId(String accommodationId);
}