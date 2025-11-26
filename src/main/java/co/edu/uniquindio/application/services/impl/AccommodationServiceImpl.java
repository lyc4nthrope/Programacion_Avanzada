package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.AccommodationDTO;
import co.edu.uniquindio.application.dto.CreateAccommodationDTO;
import co.edu.uniquindio.application.dto.EditAccommodationDTO;
import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.mappers.AccommodationMapper;
import co.edu.uniquindio.application.model.Accommodation;
import co.edu.uniquindio.application.model.AccommodationStatus;
import co.edu.uniquindio.application.services.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {

    private final AccommodationMapper accommodationMapper;
    private final Map<String, Accommodation> accommodationStore = new ConcurrentHashMap<>();

    @Override
    public void create(CreateAccommodationDTO accommodationDTO, String hostId) throws Exception {
        // Transformación del DTO a Accommodation
        Accommodation newAccommodation = accommodationMapper.toEntity(accommodationDTO);
        newAccommodation.setHostId(hostId);

        // Almacenamiento del alojamiento
        accommodationStore.put(newAccommodation.getId(), newAccommodation);
    }

    @Override
    public AccommodationDTO get(String id) throws Exception {
        // Recuperación del alojamiento
        Accommodation accommodation = accommodationStore.get(id);

        // Si el alojamiento no existe, lanzar una excepción
        if (accommodation == null) {
            throw new NotFoundException("El alojamiento con ID '" + id + "' no fue encontrado.");
        }

        // Si el alojamiento está eliminado, lanzar una excepción
        if (accommodation.getStatus() == AccommodationStatus.DELETED) {
            throw new NotFoundException("El alojamiento con ID '" + id + "' no está disponible.");
        }

        // Transformación del alojamiento a DTO
        return accommodationMapper.toAccommodationDTO(accommodation);
    }

    @Override
    public void delete(String id) throws Exception {
        // Recuperación del alojamiento
        Accommodation accommodation = accommodationStore.get(id);

        // Si el alojamiento no existe, lanzar una excepción
        if (accommodation == null) {
            throw new NotFoundException("El alojamiento con ID '" + id + "' no fue encontrado.");
        }

        // Soft delete: marcar como eliminado en lugar de eliminarlo realmente
        accommodation.setStatus(AccommodationStatus.DELETED);
        accommodationStore.put(id, accommodation);
    }

    @Override
    public List<AccommodationDTO> listAll() {
        // Obtener todos los alojamientos activos y convertirlos a DTOs
        return accommodationStore.values()
                .stream()
                .filter(a -> a.getStatus() != AccommodationStatus.DELETED)
                .map(accommodationMapper::toAccommodationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccommodationDTO> listByCity(String city) throws Exception {
        // Filtrar por ciudad
        return accommodationStore.values()
                .stream()
                .filter(a -> a.getCity().equalsIgnoreCase(city) && a.getStatus() != AccommodationStatus.DELETED)
                .map(accommodationMapper::toAccommodationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccommodationDTO> listByPriceRange(Double minPrice, Double maxPrice) throws Exception {
        // Filtrar por rango de precio
        return accommodationStore.values()
                .stream()
                .filter(a -> a.getPricePerNight().doubleValue() >= minPrice &&
                        a.getPricePerNight().doubleValue() <= maxPrice &&
                        a.getStatus() != AccommodationStatus.DELETED)
                .map(accommodationMapper::toAccommodationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void edit(String id, EditAccommodationDTO accommodationDTO) throws Exception {
        // Recuperación del alojamiento
        Accommodation accommodation = accommodationStore.get(id);

        // Si el alojamiento no existe, lanzar una excepción
        if (accommodation == null) {
            throw new NotFoundException("El alojamiento con ID '" + id + "' no fue encontrado.");
        }

        // Actualización de los campos del alojamiento
        accommodation.setTitle(accommodationDTO.title());
        accommodation.setDescription(accommodationDTO.description());
        accommodation.setCity(accommodationDTO.city());
        accommodation.setAddress(accommodationDTO.address());
        accommodation.setLatitude(accommodationDTO.latitude());
        accommodation.setLongitude(accommodationDTO.longitude());
        accommodation.setPricePerNight(accommodationDTO.pricePerNight());
        accommodation.setMaxCapacity(accommodationDTO.maxCapacity());
        accommodation.setAmenities(accommodationDTO.amenities());

        // Actualización en el almacenamiento
        accommodationStore.put(id, accommodation);
    }
}