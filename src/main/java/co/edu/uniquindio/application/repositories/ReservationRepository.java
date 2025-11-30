package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.models.entitys.Reservation;
import co.edu.uniquindio.application.models.enums.ReservationStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

    // ✅ EJERCICIO 3: Reportes de usuario con ordenamiento
    // Permite ordenar por fecha ascendente o descendente
    List<Reservation> findByGuestId(String guestId, Sort sort);

    // Consultas existentes
    List<Reservation> findByAccommodationId(String accommodationId);
    List<Reservation> findByGuestId(String guestId);
    List<Reservation> findByStatus(ReservationStatus status);

    @Query("SELECT r FROM Reservation r WHERE r.status IN ('PENDING', 'CONFIRMED') AND r.accommodation.id = :accommodationId")
    List<Reservation> findActiveReservations(@Param("accommodationId") String accommodationId);

    @Query("SELECT r FROM Reservation r WHERE r.accommodation.id = :accommodationId " +
            "AND r.checkInDate < :checkOutDate AND r.checkOutDate > :checkInDate " +
            "AND r.status NOT IN ('CANCELLED')")
    List<Reservation> findOverlappingReservations(
            @Param("accommodationId") String accommodationId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    Long countByAccommodationIdAndStatus(String accommodationId, ReservationStatus status);

    // ✅ EJERCICIO 5: Consultas personalizadas adicionales para reservas

    // 1. Buscar reservas por rango de fechas
    @Query("SELECT r FROM Reservation r WHERE r.checkInDate >= :startDate AND r.checkOutDate <= :endDate")
    List<Reservation> findByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Sort sort
    );

    // 2. Buscar reservas activas de un usuario (PENDING o CONFIRMED)
    @Query("SELECT r FROM Reservation r WHERE r.guest.id = :guestId AND r.status IN ('PENDING', 'CONFIRMED')")
    List<Reservation> findActiveReservationsByGuest(@Param("guestId") String guestId, Sort sort);

    // 3. Buscar reservas completadas de un usuario
    @Query("SELECT r FROM Reservation r WHERE r.guest.id = :guestId AND r.status = 'COMPLETED'")
    List<Reservation> findCompletedReservationsByGuest(@Param("guestId") String guestId, Sort sort);

    // 4. Buscar próximas reservas (check-in después de hoy)
    @Query("SELECT r FROM Reservation r WHERE r.checkInDate > :today AND r.status IN ('PENDING', 'CONFIRMED')")
    List<Reservation> findUpcomingReservations(@Param("today") LocalDate today, Sort sort);

    // 5. Buscar reservas por alojamiento y estado con ordenamiento
    @Query("SELECT r FROM Reservation r WHERE r.accommodation.id = :accommodationId AND r.status = :status")
    List<Reservation> findByAccommodationAndStatus(
        @Param("accommodationId") String accommodationId,
        @Param("status") ReservationStatus status,
        Sort sort
    );
}