package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.models.entitys.Accommodation;
import co.edu.uniquindio.application.models.enums.AccommodationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, String> {

    // ✅ EJERCICIO 1: Consulta por ciudad con paginación
    Page<Accommodation> findByCity(String city, Pageable pageable);

    // ✅ EJERCICIO 4: Búsqueda por texto con paginación (nombre contiene texto, ignore case)
    Page<Accommodation> findByTitleContainingIgnoreCase(String text, Pageable pageable);

    // Consultas básicas necesarias (usadas en AccommodationServiceImpl)
    Optional<Accommodation> findByTitle(String title);
    List<Accommodation> findByStatusEquals(AccommodationStatus status);

    // ✅ EJERCICIO 5: Consultas personalizadas adicionales

    // 1. Buscar alojamientos activos por ciudad con precio menor o igual
    @Query("SELECT a FROM Accommodation a WHERE a.city = :city AND a.pricePerNight <= :maxPrice AND a.status = 'ACTIVE'")
    Page<Accommodation> findActiveByCityAndMaxPrice(
            @Param("city") String city,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );

    // 2. Buscar alojamientos con capacidad mínima y rating mínimo
    @Query("SELECT a FROM Accommodation a WHERE a.maxCapacity >= :minCapacity " +
            "AND a.averageRating >= :minRating AND a.status = 'ACTIVE' ORDER BY a.averageRating DESC")
    Page<Accommodation> findByCapacityAndRating(
            @Param("minCapacity") Integer minCapacity,
            @Param("minRating") Double minRating,
            Pageable pageable
    );

    // 3. Buscar alojamientos más populares (con más reseñas)
    @Query("SELECT a FROM Accommodation a WHERE a.status = 'ACTIVE' ORDER BY a.ratingCount DESC, a.averageRating DESC")
    Page<Accommodation> findMostPopular(Pageable pageable);

    // 4. Buscar alojamientos por rango de precio con ordenamiento
    @Query("SELECT a FROM Accommodation a WHERE a.pricePerNight BETWEEN :minPrice AND :maxPrice " +
            "AND a.status = 'ACTIVE'")
    Page<Accommodation> findByPriceRangeActive(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );

    // 5. Buscar alojamientos cerca de una ubicación (radio aproximado)
    @Query("SELECT a FROM Accommodation a WHERE a.status = 'ACTIVE' AND " +
            "SQRT(POWER(a.latitude - :latitude, 2) + POWER(a.longitude - :longitude, 2)) < :radius " +
            "ORDER BY SQRT(POWER(a.latitude - :latitude, 2) + POWER(a.longitude - :longitude, 2))")
    Page<Accommodation> findNearLocation(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Double radius,
            Pageable pageable
    );
}