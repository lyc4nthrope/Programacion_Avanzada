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
        // Validar que el host existe
        Optional<User> host = userRepository.findById(hostId);
        if (host.isEmpty()) {
            throw new NotFoundException("El usuario anfitrión con ID '" + hostId + "' no fue encontrado.");
        }

        // Transformación del DTO a Accommodation
        Accommodation newAccommodation = accommodationMapper.toEntity(accommodationDTO);
        newAccommodation.setHost(host.get());

        // Almacenamiento
        accommodationRepository.save(newAccommodation);
    }

    @Override
    public AccommodationDTO get(String id) throws Exception {
        // Recuperación del alojamiento
        Optional<Accommodation> accommodationOptional = accommodationRepository.findById(id);

        // Si el alojamiento no existe, lanzar una excepción
        if (accommodationOptional.isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + id + "' no fue encontrado.");
        }

        // Transformación del alojamiento a DTO
        return accommodationMapper.toAccommodationDTO(accommodationOptional.get());
    }

    @Override
    public void delete(String id) throws Exception {
        // Recuperación del alojamiento
        Optional<Accommodation> accommodationOptional = accommodationRepository.findById(id);

        // Si el alojamiento no existe, lanzar una excepción
        if (accommodationOptional.isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + id + "' no fue encontrado.");
        }

        // Eliminación física del alojamiento
        accommodationRepository.deleteById(id);
    }

    @Override
    public List<AccommodationDTO> listAll() {
        // Obtener todos los alojamientos y convertirlos a DTOs
        return accommodationRepository.findAll()
                .stream()
                .map(accommodationMapper::toAccommodationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccommodationDTO> listByCity(String city) throws Exception {
        // Filtrar por ciudad
        return accommodationRepository.findByCity(city)
                .stream()
                .map(accommodationMapper::toAccommodationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccommodationDTO> listByPriceRange(Double minPrice, Double maxPrice) throws Exception {
        // Filtrar por rango de precio
        return accommodationRepository.findByPricePerNightBetween(minPrice, maxPrice)
                .stream()
                .map(accommodationMapper::toAccommodationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void edit(String id, EditAccommodationDTO accommodationDTO) throws Exception {
        // Recuperación del alojamiento
        Optional<Accommodation> accommodationOptional = accommodationRepository.findById(id);

        // Si el alojamiento no existe, lanzar una excepción
        if (accommodationOptional.isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + id + "' no fue encontrado.");
        }

        // Se obtiene el alojamiento que está dentro del Optional
        Accommodation accommodation = accommodationOptional.get();

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

        // Almacenamiento del alojamiento actualizado
        accommodationRepository.save(accommodation);
    }

    // ✅ NUEVOS MÉTODOS PARA GESTIONAR STATUS

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
        // Validar que el alojamiento existe
        Optional<Accommodation> accommodationOptional = accommodationRepository.findById(id);

        if (accommodationOptional.isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + id + "' no fue encontrado.");
        }

        Accommodation accommodation = accommodationOptional.get();

        // Validar que no se intente activar un alojamiento eliminado
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
}