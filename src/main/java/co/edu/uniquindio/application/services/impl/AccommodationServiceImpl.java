package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.AccommodationDTO;
import co.edu.uniquindio.application.dto.create.CreateAccommodationDTO;
import co.edu.uniquindio.application.dto.edit.EditAccommodationDTO;
import co.edu.uniquindio.application.exceptions.InvalidOperationException;
import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.mappers.AccommodationMapper;
import co.edu.uniquindio.application.models.entitys.Accommodation;
import co.edu.uniquindio.application.models.entitys.User;
import co.edu.uniquindio.application.models.enums.AccommodationStatus;
import co.edu.uniquindio.application.models.enums.Role;
import co.edu.uniquindio.application.repositories.AccommodationRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.AccommodationService;
import co.edu.uniquindio.application.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;
    private final UserRepository userRepository;
    private final AuthService authService;

    // ========================================
    // GESTIÓN DE ESTADO - CON VALIDACIONES DE PROPIETARIO
    // ========================================

    @Override
    public void activate(String id) throws Exception {
        validateOwnershipAndChangeStatus(id, AccommodationStatus.ACTIVE);
    }

    @Override
    public void deactivate(String id) throws Exception {
        validateOwnershipAndChangeStatus(id, AccommodationStatus.INACTIVE);
    }

    @Override
    public void softDelete(String id) throws Exception {
        validateOwnershipAndChangeStatus(id, AccommodationStatus.DELETED);
    }

    @Override
    public void changeStatus(String id, AccommodationStatus newStatus) throws Exception {
        validateOwnershipAndChangeStatus(id, newStatus);
    }

    private void validateOwnershipAndChangeStatus(String id, AccommodationStatus newStatus)
            throws Exception {

        // OBTENER USUARIO AUTENTICADO
        String authenticatedUserId = authService.getAuthenticatedUserId();

        // Recuperar el alojamiento
        Optional<Accommodation> accommodationOptional = accommodationRepository.findById(id);
        if (accommodationOptional.isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + id + "' no fue encontrado.");
        }

        Accommodation accommodation = accommodationOptional.get();

        // VALIDAR: Solo el propietario puede cambiar el estado de su alojamiento
        if (!accommodation.getHost().getId().equals(authenticatedUserId)) {
            throw new InvalidOperationException(
                    "Solo puedes modificar el estado de tus propios alojamientos. " +
                            "Este alojamiento pertenece a otro anfitrión."
            );
        }

        // Validar que no se active un alojamiento eliminado
        if (accommodation.getStatus() == AccommodationStatus.DELETED &&
                newStatus == AccommodationStatus.ACTIVE) {
            throw new InvalidOperationException(
                    "No se puede activar un alojamiento eliminado. Debe crearse uno nuevo."
            );
        }

        // Cambiar el estado
        accommodation.setStatus(newStatus);
        accommodationRepository.save(accommodation);
    }

    @Override
    public void create(CreateAccommodationDTO accommodationDTO, String hostId) throws Exception {
        // ✅ OBTENER USUARIO AUTENTICADO
        String authenticatedUserId = authService.getAuthenticatedUserId();

        // ✅ VALIDAR: Solo puede crear alojamientos para sí mismo
        if (!hostId.equals(authenticatedUserId)) {
            throw new InvalidOperationException(
                    "Solo puedes crear alojamientos como tu propio anfitrión. " +
                            "No puedes crear alojamientos en nombre de otros usuarios."
            );
        }

        Optional<User> host = userRepository.findById(hostId);
        if (host.isEmpty()) {
            throw new NotFoundException("El usuario anfitrión con ID '" + hostId + "' no fue encontrado.");
        }

        // ✅ VALIDAR: El usuario debe tener rol HOST
        if (host.get().getRole() != Role.HOST && host.get().getRole() != Role.ADMIN) {
            throw new InvalidOperationException(
                    "Solo los usuarios con rol HOST o ADMIN pueden crear alojamientos."
            );
        }

        Accommodation newAccommodation = accommodationMapper.toEntity(accommodationDTO);
        newAccommodation.setHost(host.get());
        accommodationRepository.save(newAccommodation);
    }

    @Override
    public AccommodationDTO get(String id) throws Exception {
        Optional<Accommodation> accommodationOptional = accommodationRepository.findById(id);
        if (accommodationOptional.isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + id + "' no fue encontrado.");
        }
        return accommodationMapper.toAccommodationDTO(accommodationOptional.get());
    }

    @Override
    public void edit(String id, EditAccommodationDTO accommodationDTO) throws Exception {
        // OBTENER USUARIO AUTENTICADO
        String authenticatedUserId = authService.getAuthenticatedUserId();

        Optional<Accommodation> accommodationOptional = accommodationRepository.findById(id);
        if (accommodationOptional.isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + id + "' no fue encontrado.");
        }

        Accommodation accommodation = accommodationOptional.get();

        // VALIDAR: Solo el anfitrión propietario puede editar su alojamiento
        if (!accommodation.getHost().getId().equals(authenticatedUserId)) {
            throw new InvalidOperationException(
                    "Solo puedes editar tus propios alojamientos. " +
                            "Este alojamiento pertenece a otro anfitrión."
            );
        }

        accommodation.setTitle(accommodationDTO.title());
        accommodation.setDescription(accommodationDTO.description());
        accommodation.setCity(accommodationDTO.city());
        accommodation.setAddress(accommodationDTO.address());
        accommodation.setLatitude(accommodationDTO.latitude());
        accommodation.setLongitude(accommodationDTO.longitude());
        accommodation.setPricePerNight(accommodationDTO.pricePerNight());
        accommodation.setMaxCapacity(accommodationDTO.maxCapacity());
        accommodation.setAmenities(accommodationDTO.amenities());
        accommodationRepository.save(accommodation);
    }

    @Override
    public void delete(String id) throws Exception {
        // OBTENER USUARIO AUTENTICADO
        String authenticatedUserId = authService.getAuthenticatedUserId();

        Optional<Accommodation> accommodationOptional = accommodationRepository.findById(id);
        if (accommodationOptional.isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + id + "' no fue encontrado.");
        }

        Accommodation accommodation = accommodationOptional.get();

        // VALIDAR: Solo el anfitrión propietario puede eliminar su alojamiento
        if (!accommodation.getHost().getId().equals(authenticatedUserId)) {
            throw new InvalidOperationException(
                    "Solo puedes eliminar tus propios alojamientos."
            );
        }

        accommodationRepository.deleteById(id);
    }

    // ========================================
    // CONSULTAS SIN PAGINACIÓN (LISTAS SIMPLES)
    // ========================================

    @Override
    public List<AccommodationDTO> listAll() {
        return accommodationRepository.findAll()
                .stream()
                .map(accommodationMapper::toAccommodationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccommodationDTO> listActive() {
        return listByStatus(AccommodationStatus.ACTIVE);
    }

    @Override
    public List<AccommodationDTO> listByStatus(AccommodationStatus status) {
        return accommodationRepository.findByStatusEquals(status)
                .stream()
                .map(accommodationMapper::toAccommodationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccommodationDTO> listByCity(String city) {
        Page<Accommodation> page = accommodationRepository.findByCity(city, Pageable.unpaged());
        return page.getContent()
                .stream()
                .map(accommodationMapper::toAccommodationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccommodationDTO> listByPriceRange(Double minPrice, Double maxPrice) {
        Page<Accommodation> page = accommodationRepository.findByPricePerNightBetween(minPrice, maxPrice, Pageable.unpaged());
        return page.getContent()
                .stream()
                .map(accommodationMapper::toAccommodationDTO)
                .collect(Collectors.toList());
    }

    // ========================================
    // CONSULTAS CON PAGINACIÓN
    // ========================================

    @Override
    public Page<AccommodationDTO> listAllPaginated(Pageable pageable) {
        Page<Accommodation> page = accommodationRepository.findAll(pageable);
        return page.map(accommodationMapper::toAccommodationDTO);
    }

    @Override
    public Page<AccommodationDTO> listByStatusPaginated(AccommodationStatus status, Pageable pageable) {
        Page<Accommodation> page = accommodationRepository.findByStatusEquals(status, pageable);
        return page.map(accommodationMapper::toAccommodationDTO);
    }

    @Override
    public Page<AccommodationDTO> listByCityPaginated(String city, Pageable pageable) throws Exception {
        Page<Accommodation> page = accommodationRepository.findByCity(city, pageable);
        return page.map(accommodationMapper::toAccommodationDTO);
    }

    @Override
    public Page<AccommodationDTO> listByPriceRangePaginated(Double minPrice, Double maxPrice, Pageable pageable) {
        Page<Accommodation> page = accommodationRepository.findByPricePerNightBetween(minPrice, maxPrice, pageable);
        return page.map(accommodationMapper::toAccommodationDTO);
    }

    // ========================================
    // EJERCICIO 4: BÚSQUEDA POR TEXTO
    // ========================================

    @Override
    public Page<AccommodationDTO> searchByNamePaginated(String text, Pageable pageable) throws Exception {
        Page<Accommodation> page = accommodationRepository.findByTitleContainingIgnoreCase(text, pageable);
        return page.map(accommodationMapper::toAccommodationDTO);
    }

    // ========================================
    // EJERCICIO 5: CONSULTAS PERSONALIZADAS
    // ========================================

    @Override
    public Page<AccommodationDTO> findActiveByCityAndMaxPrice(String city, Double maxPrice, Pageable pageable) {
        Page<Accommodation> page = accommodationRepository.findActiveByCityAndMaxPrice(city, maxPrice, pageable);
        return page.map(accommodationMapper::toAccommodationDTO);
    }

    @Override
    public Page<AccommodationDTO> findByCapacityAndRating(Integer minCapacity, Double minRating, Pageable pageable) {
        Page<Accommodation> page = accommodationRepository.findByCapacityAndRating(minCapacity, minRating, pageable);
        return page.map(accommodationMapper::toAccommodationDTO);
    }

    @Override
    public Page<AccommodationDTO> findMostPopular(Pageable pageable) {
        Page<Accommodation> page = accommodationRepository.findMostPopular(pageable);
        return page.map(accommodationMapper::toAccommodationDTO);
    }

    @Override
    public Page<AccommodationDTO> findByPriceRangeActive(Double minPrice, Double maxPrice, Pageable pageable) {
        Page<Accommodation> page = accommodationRepository.findByPriceRangeActive(minPrice, maxPrice, pageable);
        return page.map(accommodationMapper::toAccommodationDTO);
    }

    @Override
    public Page<AccommodationDTO> findNearLocation(Double latitude, Double longitude, Double radiusKm, Pageable pageable) {
        // Convertir km a grados aproximados (1 grado ≈ 111 km)
        Double radiusDegrees = radiusKm / 111.0;
        Page<Accommodation> page = accommodationRepository.findNearLocation(latitude, longitude, radiusDegrees, pageable);
        return page.map(accommodationMapper::toAccommodationDTO);
    }
}