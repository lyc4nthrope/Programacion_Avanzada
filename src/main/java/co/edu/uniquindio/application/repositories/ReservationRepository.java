package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.models.entitys.Reservation;
import co.edu.uniquindio.application.models.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

    // Buscar reservas por alojamiento
    List<Reservation> findByAccommodationId(String accommodationId);

    // Buscar reservas por huésped
    List<Reservation> findByGuestId(String guestId);

    // Buscar reservas por estado
    List<Reservation> findByStatus(ReservationStatus status);

    // Buscar reservas activas (confirmadas o pendientes)
    @Query("SELECT r FROM Reservation r WHERE r.status IN ('PENDING', 'CONFIRMED') AND r.accommodationId = :accommodationId")
    List<Reservation> findActiveReservations(@Param("accommodationId") String accommodationId);

    // Buscar reservas que se solapan en fechas
    @Query("SELECT r FROM Reservation r WHERE r.accommodationId = :accommodationId " +
            "AND r.checkInDate < :checkOutDate AND r.checkOutDate > :checkInDate " +
            "AND r.status NOT IN ('CANCELLED')")
    List<Reservation> findOverlappingReservations(
            @Param("accommodationId") String accommodationId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    // Contar reservas confirmadas por alojamiento
    Long countByAccommodationIdAndStatus(String accommodationId, ReservationStatus status);
}