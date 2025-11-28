package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.create.CreateReservationDTO;
import co.edu.uniquindio.application.dto.edit.EditReservationDTO;
import co.edu.uniquindio.application.dto.ReservationDTO;
import co.edu.uniquindio.application.exceptions.InvalidOperationException;
import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.mappers.ReservationMapper;
import co.edu.uniquindio.application.models.entitys.Accommodation;
import co.edu.uniquindio.application.models.entitys.Reservation;
import co.edu.uniquindio.application.models.entitys.User;
import co.edu.uniquindio.application.models.enums.ReservationStatus;
import co.edu.uniquindio.application.repositories.AccommodationRepository;
import co.edu.uniquindio.application.repositories.ReservationRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final AccommodationRepository accommodationRepository;
    private final UserRepository userRepository;

    @Override
    public void create(CreateReservationDTO reservationDTO) throws Exception {
        // Validar que las fechas sean válidas
        if (reservationDTO.checkInDate().isAfter(reservationDTO.checkOutDate())) {
            throw new InvalidOperationException("La fecha de entrada no puede ser posterior a la fecha de salida.");
        }

        if (reservationDTO.checkInDate().isEqual(reservationDTO.checkOutDate())) {
            throw new InvalidOperationException("La fecha de entrada y salida no pueden ser iguales.");
        }

        // Validar que el alojamiento existe
        Optional<Accommodation> accommodation = accommodationRepository.findById(reservationDTO.accommodationId());
        if (accommodation.isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + reservationDTO.accommodationId() + "' no fue encontrado.");
        }

        // Validar que el huésped existe
        Optional<User> guest = userRepository.findById(reservationDTO.guestId());
        if (guest.isEmpty()) {
            throw new NotFoundException("El huésped con ID '" + reservationDTO.guestId() + "' no fue encontrado.");
        }

        // Validar capacidad máxima
        if (reservationDTO.numberOfGuests() > accommodation.get().getMaxCapacity()) {
            throw new InvalidOperationException("El número de huéspedes (" + reservationDTO.numberOfGuests() +
                    ") excede la capacidad máxima (" + accommodation.get().getMaxCapacity() + ").");
        }

        // Verificar disponibilidad
        if (!isAvailable(reservationDTO.accommodationId(), reservationDTO.checkInDate(), reservationDTO.checkOutDate())) {
            throw new InvalidOperationException("El alojamiento no está disponible para las fechas seleccionadas.");
        }

        // Crear la reserva
        Reservation newReservation = reservationMapper.toEntity(reservationDTO);
        newReservation.setAccommodation(accommodation.get());
        newReservation.setGuest(guest.get());

        // Calcular precio total
        long nights = java.time.temporal.ChronoUnit.DAYS.between(
                reservationDTO.checkInDate(),
                reservationDTO.checkOutDate()
        );
        double totalPrice = accommodation.get().getPricePerNight() * nights;
        newReservation.setTotalPrice(totalPrice);

        // Guardar la reserva
        reservationRepository.save(newReservation);
    }

    @Override
    public ReservationDTO get(String id) throws Exception {
        Optional<Reservation> reservationOptional = reservationRepository.findById(id);

        if (reservationOptional.isEmpty()) {
            throw new NotFoundException("La reserva con ID '" + id + "' no fue encontrada.");
        }

        return reservationMapper.toReservationDTO(reservationOptional.get());
    }

    @Override
    public void delete(String id) throws Exception {
        Optional<Reservation> reservationOptional = reservationRepository.findById(id);

        if (reservationOptional.isEmpty()) {
            throw new NotFoundException("La reserva con ID '" + id + "' no fue encontrada.");
        }

        reservationRepository.deleteById(id);
    }

    @Override
    public List<ReservationDTO> listAll() {
        return reservationRepository.findAll()
                .stream()
                .map(reservationMapper::toReservationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDTO> listByAccommodation(String accommodationId) throws Exception {
        if (accommodationRepository.findById(accommodationId).isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + accommodationId + "' no fue encontrado.");
        }

        return reservationRepository.findByAccommodationId(accommodationId)
                .stream()
                .map(reservationMapper::toReservationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDTO> listByGuest(String guestId) throws Exception {
        if (userRepository.findById(guestId).isEmpty()) {
            throw new NotFoundException("El huésped con ID '" + guestId + "' no fue encontrado.");
        }

        return reservationRepository.findByGuestId(guestId)
                .stream()
                .map(reservationMapper::toReservationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDTO> listByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status)
                .stream()
                .map(reservationMapper::toReservationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void edit(String id, EditReservationDTO reservationDTO) throws Exception {
        Optional<Reservation> reservationOptional = reservationRepository.findById(id);

        if (reservationOptional.isEmpty()) {
            throw new NotFoundException("La reserva con ID '" + id + "' no fue encontrada.");
        }

        Reservation reservation = reservationOptional.get();

        // Validar que no esté cancelada
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new InvalidOperationException("No se puede editar una reserva cancelada.");
        }

        // Validar que no haya iniciado
        if (reservation.getCheckInDate().isBefore(LocalDate.now())) {
            throw new InvalidOperationException("No se puede editar una reserva que ya ha iniciado.");
        }

        // Validar fechas
        if (reservationDTO.checkInDate().isAfter(reservationDTO.checkOutDate())) {
            throw new InvalidOperationException("La fecha de entrada no puede ser posterior a la fecha de salida.");
        }

        // Validar capacidad
        if (reservationDTO.numberOfGuests() > reservation.getAccommodation().getMaxCapacity()) {
            throw new InvalidOperationException("El número de huéspedes excede la capacidad máxima.");
        }

        // Actualizar campos
        reservation.setCheckInDate(reservationDTO.checkInDate());
        reservation.setCheckOutDate(reservationDTO.checkOutDate());
        reservation.setNumberOfGuests(reservationDTO.numberOfGuests());
        reservation.setStatus(reservationDTO.status());

        // Recalcular precio
        long nights = java.time.temporal.ChronoUnit.DAYS.between(
                reservationDTO.checkInDate(),
                reservationDTO.checkOutDate()
        );
        double totalPrice = reservation.getAccommodation().getPricePerNight() * nights;
        reservation.setTotalPrice(totalPrice);

        reservationRepository.save(reservation);
    }

    @Override
    public void confirm(String id) throws Exception {
        Optional<Reservation> reservationOptional = reservationRepository.findById(id);

        if (reservationOptional.isEmpty()) {
            throw new NotFoundException("La reserva con ID '" + id + "' no fue encontrada.");
        }

        Reservation reservation = reservationOptional.get();

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new InvalidOperationException("Solo se pueden confirmar reservas en estado PENDING.");
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);
    }

    @Override
    public void cancel(String id) throws Exception {
        Optional<Reservation> reservationOptional = reservationRepository.findById(id);

        if (reservationOptional.isEmpty()) {
            throw new NotFoundException("La reserva con ID '" + id + "' no fue encontrada.");
        }

        Reservation reservation = reservationOptional.get();

        if (reservation.getCheckInDate().isBefore(LocalDate.now())) {
            throw new InvalidOperationException("No se puede cancelar una reserva que ya ha iniciado.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    @Override
    public boolean isAvailable(String accommodationId, LocalDate checkInDate, LocalDate checkOutDate) throws Exception {
        if (accommodationRepository.findById(accommodationId).isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + accommodationId + "' no fue encontrado.");
        }

        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(
                accommodationId,
                checkInDate,
                checkOutDate
        );

        return overlappingReservations.isEmpty();
    }
}