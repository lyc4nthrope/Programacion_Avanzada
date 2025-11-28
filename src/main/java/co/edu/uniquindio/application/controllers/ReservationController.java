package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.create.CreateReservationDTO;
import co.edu.uniquindio.application.dto.edit.EditReservationDTO;
import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.dto.ReservationDTO;
import co.edu.uniquindio.application.models.enums.ReservationStatus;
import co.edu.uniquindio.application.services.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody CreateReservationDTO reservationDTO) throws Exception {
        reservationService.create(reservationDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(false, "La reserva ha sido creada exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<ReservationDTO>> get(@PathVariable String id) throws Exception {
        ReservationDTO reservationDTO = reservationService.get(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, reservationDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> delete(@PathVariable String id) throws Exception {
        reservationService.delete(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "La reserva ha sido eliminada"));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<ReservationDTO>>> listAll() {
        List<ReservationDTO> list = reservationService.listAll();
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @GetMapping("/accommodation/{accommodationId}")
    public ResponseEntity<ResponseDTO<List<ReservationDTO>>> listByAccommodation(@PathVariable String accommodationId) throws Exception {
        List<ReservationDTO> list = reservationService.listByAccommodation(accommodationId);
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @GetMapping("/guest/{guestId}")
    public ResponseEntity<ResponseDTO<List<ReservationDTO>>> listByGuest(@PathVariable String guestId) throws Exception {
        List<ReservationDTO> list = reservationService.listByGuest(guestId);
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ResponseDTO<List<ReservationDTO>>> listByStatus(@PathVariable ReservationStatus status) {
        List<ReservationDTO> list = reservationService.listByStatus(status);
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> edit(@PathVariable String id, @Valid @RequestBody EditReservationDTO reservationDTO) throws Exception {
        reservationService.edit(id, reservationDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, "La reserva ha sido actualizada"));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ResponseDTO<String>> confirm(@PathVariable String id) throws Exception {
        reservationService.confirm(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "La reserva ha sido confirmada"));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ResponseDTO<String>> cancel(@PathVariable String id) throws Exception {
        reservationService.cancel(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "La reserva ha sido cancelada"));
    }

    @GetMapping("/availability")
    public ResponseEntity<ResponseDTO<Boolean>> checkAvailability(
            @RequestParam String accommodationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate) throws Exception {
        boolean available = reservationService.isAvailable(accommodationId, checkInDate, checkOutDate);
        return ResponseEntity.ok(new ResponseDTO<>(false, available));
    }
}