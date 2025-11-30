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
import co.edu.uniquindio.application.repositories.AccommodationRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.AccommodationService;
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

    @Override
    public void create(CreateAccommodationDTO accommodationDTO, String hostId) throws Exception {
        Optional<User> host = userRepository.findById(hostId);
        if (host.isEmpty()) {
            throw new NotFoundException("El usuario anfitrión con ID '" + hostId + "' no fue encontrado.");
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
    public void delete(String id) throws Exception {
        Optional<Accommodation> accommodationOptional = accommodationRepository.findById(id);
        if (accommodationOptional.isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + id + "' no fue encontrado.");
        }
        accommodationRepository.deleteById(id);
    }

    @Override
    public List<AccommodationDTO> listAll() {
        return accommodationRepository.findAll()
                .stream()
                .map(accommodationMapper::toAccommodationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void edit(String id, EditAccommodationDTO accommodationDTO) throws Exception {
        Optional<Accommodation> accommodationOptional = accommodationRepository.findById(id);
        if (accommodationOptional.isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + id + "' no fue encontrado.");
        }

        Accommodation accommodation = accommodationOptional.get();
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
    public void activate(String id) throws Exception {
        changeStatus(id, AccommodationStatus.ACTIVE);
    }

    @Override
    public void deactivate(String id) throws Exception {
        changeStatus(id, AccommodationStatus.INACTIVE);
    }

    @Override
    public void softDelete(String id) throws Exception {
        changeStatus(id, AccommodationStatus.DELETED);
    }

    @Override
    public List<AccommodationDTO> listByStatus(AccommodationStatus status) {
        return accommodationRepository.findByStatusEquals(status)
                .stream()
                .map(accommodationMapper::toAccommodationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccommodationDTO> listActive() {
        return listByStatus(AccommodationStatus.ACTIVE);
    }

    @Override
    public void changeStatus(String id, AccommodationStatus newStatus) throws Exception {
        Optional<Accommodation> accommodationOptional = accommodationRepository.findById(id);
        if (accommodationOptional.isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + id + "' no fue encontrado.");
        }

        Accommodation accommodation = accommodationOptional.get();
        if (accommodation.getStatus() == AccommodationStatus.DELETED && 
            newStatus == AccommodationStatus.ACTIVE) {
            throw new InvalidOperationException(
                "No se puede activar un alojamiento eliminado. Debe crearse uno nuevo."
            );
        }

        accommodation.setStatus(newStatus);
        accommodationRepository.save(accommodation);
    }

    // ✅ EJERCICIO 1: Consulta por ciudad con paginación
    @Override
    public Page<AccommodationDTO> listByCityPaginated(String city, Pageable pageable) throws Exception {
        Page<Accommodation> page = accommodationRepository.findByCity(city, pageable);
        return page.map(accommodationMapper::toAccommodationDTO);
    }

    // ✅ EJERCICIO 4: Búsqueda por texto con paginación
    @Override
    public Page<AccommodationDTO> searchByNamePaginated(String text, Pageable pageable) throws Exception {
        Page<Accommodation> page = accommodationRepository.findByTitleContainingIgnoreCase(text, pageable);
        return page.map(accommodationMapper::toAccommodationDTO);
    }

    // ✅ EJERCICIO 5: Consultas personalizadas
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