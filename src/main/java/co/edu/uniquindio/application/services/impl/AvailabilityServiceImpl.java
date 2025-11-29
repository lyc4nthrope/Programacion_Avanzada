package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.create.CreateAvailabilityDTO;
import co.edu.uniquindio.application.dto.edit.EditAvailabilityDTO;
import co.edu.uniquindio.application.dto.AvailabilityDTO;
import co.edu.uniquindio.application.exceptions.InvalidOperationException;
import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.mappers.AvailabilityMapper;
import co.edu.uniquindio.application.models.entitys.Accommodation;
import co.edu.uniquindio.application.models.entitys.AvailabilityCalendar;
import co.edu.uniquindio.application.repositories.AccommodationRepository;
import co.edu.uniquindio.application.repositories.AvailabilityCalendarRepository;
import co.edu.uniquindio.application.services.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilityCalendarRepository availabilityCalendarRepository;
    private final AvailabilityMapper availabilityMapper;
    private final AccommodationRepository accommodationRepository;

    @Override
    public void create(CreateAvailabilityDTO availabilityDTO) throws Exception {
        // Validar que el alojamiento existe
        Optional<Accommodation> accommodation = accommodationRepository.findById(availabilityDTO.accommodationId());
        if (accommodation.isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + availabilityDTO.accommodationId() + "' no fue encontrado.");
        }

        // Validar que la fecha no sea en el pasado
        if (availabilityDTO.date().isBefore(LocalDate.now())) {
            throw new InvalidOperationException("No puedes crear disponibilidad para fechas pasadas.");
        }

        // Validar que no exista ya una disponibilidad para esa fecha
        Optional<AvailabilityCalendar> existingAvailability = 
            availabilityCalendarRepository.findByAccommodationIdAndDate(
                availabilityDTO.accommodationId(),
                availabilityDTO.date()
            );
        if (existingAvailability.isPresent()) {
            throw new InvalidOperationException("Ya existe una disponibilidad registrada para esta fecha.");
        }

        // Crear la disponibilidad
        AvailabilityCalendar newAvailability = availabilityMapper.toEntity(availabilityDTO);
        newAvailability.setAccommodation(accommodation.get());

        // Guardar
        availabilityCalendarRepository.save(newAvailability);
    }

    @Override
    public AvailabilityDTO get(String id) throws Exception {
        Optional<AvailabilityCalendar> availabilityOptional = availabilityCalendarRepository.findById(id);

        if (availabilityOptional.isEmpty()) {
            throw new NotFoundException("La disponibilidad con ID '" + id + "' no fue encontrada.");
        }

        return availabilityMapper.toAvailabilityDTO(availabilityOptional.get());
    }

    @Override
    public void delete(String id) throws Exception {
        Optional<AvailabilityCalendar> availabilityOptional = availabilityCalendarRepository.findById(id);

        if (availabilityOptional.isEmpty()) {
            throw new NotFoundException("La disponibilidad con ID '" + id + "' no fue encontrada.");
        }

        availabilityCalendarRepository.deleteById(id);
    }

    @Override
    public List<AvailabilityDTO> listAll() {
        return availabilityCalendarRepository.findAll()
                .stream()
                .map(availabilityMapper::toAvailabilityDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AvailabilityDTO> listByAccommodation(String accommodationId) throws Exception {
        if (accommodationRepository.findById(accommodationId).isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + accommodationId + "' no fue encontrado.");
        }

        return availabilityCalendarRepository.findByAccommodationId(accommodationId)
                .stream()
                .map(availabilityMapper::toAvailabilityDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AvailabilityDTO> listAvailableDates(String accommodationId) throws Exception {
        if (accommodationRepository.findById(accommodationId).isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + accommodationId + "' no fue encontrado.");
        }

        return availabilityCalendarRepository.findAvailableDatesByAccommodation(accommodationId)
                .stream()
                .map(availabilityMapper::toAvailabilityDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AvailabilityDTO> listUnavailableDates(String accommodationId) throws Exception {
        if (accommodationRepository.findById(accommodationId).isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + accommodationId + "' no fue encontrado.");
        }

        return availabilityCalendarRepository.findUnavailableDatesByAccommodation(accommodationId)
                .stream()
                .map(availabilityMapper::toAvailabilityDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AvailabilityDTO> listByDateRange(String accommodationId, LocalDate startDate, LocalDate endDate) throws Exception {
        if (accommodationRepository.findById(accommodationId).isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + accommodationId + "' no fue encontrado.");
        }

        if (startDate.isAfter(endDate)) {
            throw new InvalidOperationException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        return availabilityCalendarRepository.findByDateRange(accommodationId, startDate, endDate)
                .stream()
                .map(availabilityMapper::toAvailabilityDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void edit(String id, EditAvailabilityDTO availabilityDTO) throws Exception {
        Optional<AvailabilityCalendar> availabilityOptional = availabilityCalendarRepository.findById(id);

        if (availabilityOptional.isEmpty()) {
            throw new NotFoundException("La disponibilidad con ID '" + id + "' no fue encontrada.");
        }

        AvailabilityCalendar availability = availabilityOptional.get();
        availability.setAvailable(availabilityDTO.available());
        availability.setReason(availabilityDTO.reason());

        availabilityCalendarRepository.save(availability);
    }

    @Override
    public boolean isDateAvailable(String accommodationId, LocalDate date) throws Exception {
        if (accommodationRepository.findById(accommodationId).isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + accommodationId + "' no fue encontrado.");
        }

        return availabilityCalendarRepository.isDateAvailable(accommodationId, date);
    }

    @Override
    public void blockDate(String accommodationId, LocalDate date, String reason) throws Exception {
        if (accommodationRepository.findById(accommodationId).isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + accommodationId + "' no fue encontrado.");
        }

        Optional<AvailabilityCalendar> existingAvailability = 
            availabilityCalendarRepository.findByAccommodationIdAndDate(accommodationId, date);

        if (existingAvailability.isPresent()) {
            AvailabilityCalendar availability = existingAvailability.get();
            availability.setAvailable(false);
            availability.setReason(reason);
            availabilityCalendarRepository.save(availability);
        } else {
            CreateAvailabilityDTO createDTO = new CreateAvailabilityDTO(accommodationId, date, false, reason);
            create(createDTO);
        }
    }

    @Override
    public void unblockDate(String accommodationId, LocalDate date) throws Exception {
        if (accommodationRepository.findById(accommodationId).isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + accommodationId + "' no fue encontrado.");
        }

        Optional<AvailabilityCalendar> existingAvailability = 
            availabilityCalendarRepository.findByAccommodationIdAndDate(accommodationId, date);

        if (existingAvailability.isPresent()) {
            AvailabilityCalendar availability = existingAvailability.get();
            availability.setAvailable(true);
            availability.setReason(null);
            availabilityCalendarRepository.save(availability);
        }
    }

    @Override
    public Long countAvailableDates(String accommodationId) throws Exception {
        if (accommodationRepository.findById(accommodationId).isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + accommodationId + "' no fue encontrado.");
        }

        return availabilityCalendarRepository.countByAccommodationIdAndAvailableTrue(accommodationId);
    }

    @Override
    public Long countUnavailableDates(String accommodationId) throws Exception {
        if (accommodationRepository.findById(accommodationId).isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + accommodationId + "' no fue encontrado.");
        }

        return availabilityCalendarRepository.countByAccommodationIdAndAvailableFalse(accommodationId);
    }
}