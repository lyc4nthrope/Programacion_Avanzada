package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.models.entitys.Accommodation;
import co.edu.uniquindio.application.models.enums.AccommodationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, String> {

    // Buscar alojamientos por ciudad
    List<Accommodation> findByCity(String city);

    // Buscar alojamientos por rango de precio
    List<Accommodation> findByPricePerNightBetween(Double minPrice, Double maxPrice);

    // Buscar alojamiento por título
    Optional<Accommodation> findByTitle(String title);

    // Buscar alojamientos por capacidad mínima
    List<Accommodation> findByMaxCapacityGreaterThanEqual(Integer capacity);

    // Buscar alojamientos activos (si implementas soft delete)
    List<Accommodation> findByStatusEquals(AccommodationStatus status);

    // Buscar por rating mínimo
    @Query("SELECT a FROM Accommodation a WHERE a.averageRating >= :minRating")
    List<Accommodation> findByMinimumRating(@Param("minRating") Double minRating);
}