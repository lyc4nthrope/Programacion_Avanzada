package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.create.CreateReservationDTO;
import co.edu.uniquindio.application.dto.edit.EditReservationDTO;
import co.edu.uniquindio.application.dto.ReservationDTO;
import co.edu.uniquindio.application.models.enums.ReservationStatus;

import java.util.List;

public interface ReservationService {

    // Crear una nueva reserva
    void create(CreateReservationDTO reservationDTO) throws Exception;

    // Obtener una reserva por ID
    ReservationDTO get(String id) throws Exception;

    // Eliminar una reserva
    void delete(String id) throws Exception;

    // Listar todas las reservas
    List<ReservationDTO> listAll();

    // Listar reservas de un alojamiento
    List<ReservationDTO> listByAccommodation(String accommodationId) throws Exception;

    // Listar reservas de un huésped
    List<ReservationDTO> listByGuest(String guestId) throws Exception;

    // Listar reservas por estado
    List<ReservationDTO> listByStatus(ReservationStatus status);

    // Editar una reserva
    void edit(String id, EditReservationDTO reservationDTO) throws Exception;

    // Confirmar una reserva
    void confirm(String id) throws Exception;

    // Cancelar una reserva
    void cancel(String id) throws Exception;

    // Verificar disponibilidad de fechas
    boolean isAvailable(String accommodationId, java.time.LocalDate checkInDate, java.time.LocalDate checkOutDate) throws Exception;
}