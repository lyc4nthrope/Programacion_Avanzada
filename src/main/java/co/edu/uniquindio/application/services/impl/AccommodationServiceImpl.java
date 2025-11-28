package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.AccommodationDTO;
import co.edu.uniquindio.application.dto.create.CreateAccommodationDTO;
import co.edu.uniquindio.application.dto.edit.EditAccommodationDTO;
import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.mappers.AccommodationMapper;
import co.edu.uniquindio.application.models.entitys.Accommodation;
import co.edu.uniquindio.application.repositories.AccommodationRepository;
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

    @Override
    public void create(CreateAccommodationDTO accommodationDTO, String hostId) throws Exception {
        // Transformación del DTO a Accommodation
        Accommodation newAccommodation = accommodationMapper.toEntity(accommodationDTO);

        // Almacenamiento del alojamiento en la base de datos
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

        // Eliminación del alojamiento
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
}