package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.models.entitys.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
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
}