package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.AccommodationDTO;
import co.edu.uniquindio.application.dto.create.CreateAccommodationDTO;
import co.edu.uniquindio.application.dto.edit.EditAccommodationDTO;
import co.edu.uniquindio.application.models.enums.AccommodationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccommodationService {

    void create(CreateAccommodationDTO accommodationDTO, String hostId) throws Exception;
    AccommodationDTO get(String id) throws Exception;
    void delete(String id) throws Exception;
    void edit(String id, EditAccommodationDTO accommodationDTO) throws Exception;

    // Gestión de status
    void activate(String id) throws Exception;
    void deactivate(String id) throws Exception;
    void softDelete(String id) throws Exception;
    void changeStatus(String id, AccommodationStatus newStatus) throws Exception;

    // ✅ Métodos SIN paginación (para listas simples)
    List<AccommodationDTO> listAll();
    List<AccommodationDTO> listByStatus(AccommodationStatus status);
    List<AccommodationDTO> listActive();
    List<AccommodationDTO> listByCity(String city);
    List<AccommodationDTO> listByPriceRange(Double minPrice, Double maxPrice);

    // ✅ Métodos CON paginación (para consultas grandes)
    Page<AccommodationDTO> listAllPaginated(Pageable pageable);
    Page<AccommodationDTO> listByStatusPaginated(AccommodationStatus status, Pageable pageable);
    Page<AccommodationDTO> listByCityPaginated(String city, Pageable pageable) throws Exception;
    Page<AccommodationDTO> listByPriceRangePaginated(Double minPrice, Double maxPrice, Pageable pageable);

    // ✅ EJERCICIO 4: Búsqueda por texto con paginación
    Page<AccommodationDTO> searchByNamePaginated(String text, Pageable pageable) throws Exception;

    // ✅ EJERCICIO 5: Consultas personalizadas
    Page<AccommodationDTO> findActiveByCityAndMaxPrice(String city, Double maxPrice, Pageable pageable);
    Page<AccommodationDTO> findByCapacityAndRating(Integer minCapacity, Double minRating, Pageable pageable);
    Page<AccommodationDTO> findMostPopular(Pageable pageable);
    Page<AccommodationDTO> findByPriceRangeActive(Double minPrice, Double maxPrice, Pageable pageable);
    Page<AccommodationDTO> findNearLocation(Double latitude, Double longitude, Double radiusKm, Pageable pageable);
}