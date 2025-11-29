package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.models.entitys.AvailabilityCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilityCalendarRepository extends JpaRepository<AvailabilityCalendar, String> {

    // Buscar disponibilidad por alojamiento
    List<AvailabilityCalendar> findByAccommodationId(String accommodationId);

    // Buscar disponibilidad por alojamiento y fecha
    Optional<AvailabilityCalendar> findByAccommodationIdAndDate(String accommodationId, LocalDate date);

    // Buscar disponibilidad disponibles de un alojamiento
    @Query("SELECT a FROM AvailabilityCalendar a WHERE a.accommodation.id = :accommodationId AND a.available = true")
    List<AvailabilityCalendar> findAvailableDatesByAccommodation(@Param("accommodationId") String accommodationId);

    // Buscar disponibilidad no disponibles de un alojamiento
    @Query("SELECT a FROM AvailabilityCalendar a WHERE a.accommodation.id = :accommodationId AND a.available = false")
    List<AvailabilityCalendar> findUnavailableDatesByAccommodation(@Param("accommodationId") String accommodationId);

    // Buscar disponibilidad en rango de fechas
    @Query("SELECT a FROM AvailabilityCalendar a WHERE a.accommodation.id = :accommodationId " +
           "AND a.date BETWEEN :startDate AND :endDate")
    List<AvailabilityCalendar> findByDateRange(@Param("accommodationId") String accommodationId,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    // Contar disponibilidades disponibles de un alojamiento
    Long countByAccommodationIdAndAvailableTrue(String accommodationId);

    // Contar disponibilidades no disponibles de un alojamiento
    Long countByAccommodationIdAndAvailableFalse(String accommodationId);

    // Verificar si una fecha está disponible
    @Query("SELECT COUNT(a) > 0 FROM AvailabilityCalendar a WHERE a.accommodation.id = :accommodationId " +
           "AND a.date = :date AND a.available = true")
    boolean isDateAvailable(@Param("accommodationId") String accommodationId, @Param("date") LocalDate date);
}