package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.AccommodationDTO;
import co.edu.uniquindio.application.dto.create.CreateAccommodationDTO;
import co.edu.uniquindio.application.dto.edit.EditAccommodationDTO;
import co.edu.uniquindio.application.models.enums.AccommodationStatus;
import java.util.List;

public interface AccommodationService {

    void create(CreateAccommodationDTO accommodationDTO, String hostId) throws Exception;

    AccommodationDTO get(String id) throws Exception;

    void delete(String id) throws Exception;

    List<AccommodationDTO> listAll();

    List<AccommodationDTO> listByCity(String city) throws Exception;

    List<AccommodationDTO> listByPriceRange(Double minPrice, Double maxPrice) throws Exception;

    void edit(String id, EditAccommodationDTO accommodationDTO) throws Exception;

    // ✅ NUEVOS MÉTODOS PARA GESTIONAR STATUS

    /**
     * Activa un alojamiento (cambia status a ACTIVE)
     */
    void activate(String id) throws Exception;

    /**
     * Desactiva un alojamiento (cambia status a INACTIVE)
     */
    void deactivate(String id) throws Exception;

    /**
     * Marca un alojamiento como eliminado (soft delete - cambia status a DELETED)
     */
    void softDelete(String id) throws Exception;

    /**
     * Lista alojamientos por estado
     */
    List<AccommodationDTO> listByStatus(AccommodationStatus status);

    /**
     * Lista solo alojamientos activos
     */
    List<AccommodationDTO> listActive();

    /**
     * Cambia el estado de un alojamiento
     */
    void changeStatus(String id, AccommodationStatus newStatus) throws Exception;
}