package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.create.CreateAvailabilityDTO;
import co.edu.uniquindio.application.dto.edit.EditAvailabilityDTO;
import co.edu.uniquindio.application.dto.AvailabilityDTO;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityService {

    // Crear una disponibilidad
    void create(CreateAvailabilityDTO availabilityDTO) throws Exception;

    // Obtener una disponibilidad por ID
    AvailabilityDTO get(String id) throws Exception;

    // Eliminar una disponibilidad
    void delete(String id) throws Exception;

    // Listar todas las disponibilidades
    List<AvailabilityDTO> listAll();

    // Listar disponibilidades de un alojamiento
    List<AvailabilityDTO> listByAccommodation(String accommodationId) throws Exception;

    // Listar fechas disponibles de un alojamiento
    List<AvailabilityDTO> listAvailableDates(String accommodationId) throws Exception;

    // Listar fechas no disponibles de un alojamiento
    List<AvailabilityDTO> listUnavailableDates(String accommodationId) throws Exception;

    // Listar disponibilidades en rango de fechas
    List<AvailabilityDTO> listByDateRange(String accommodationId, LocalDate startDate, LocalDate endDate) throws Exception;

    // Editar una disponibilidad
    void edit(String id, EditAvailabilityDTO availabilityDTO) throws Exception;

    // Verificar si una fecha está disponible
    boolean isDateAvailable(String accommodationId, LocalDate date) throws Exception;

    // Marcar fecha como no disponible (bloqueada)
    void blockDate(String accommodationId, LocalDate date, String reason) throws Exception;

    // Desbloquear una fecha
    void unblockDate(String accommodationId, LocalDate date) throws Exception;

    // Contar fechas disponibles
    Long countAvailableDates(String accommodationId) throws Exception;

    // Contar fechas no disponibles
    Long countUnavailableDates(String accommodationId) throws Exception;
}