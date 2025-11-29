package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.create.CreateAvailabilityDTO;
import co.edu.uniquindio.application.dto.edit.EditAvailabilityDTO;
import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.dto.AvailabilityDTO;
import co.edu.uniquindio.application.services.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody CreateAvailabilityDTO availabilityDTO) throws Exception {
        availabilityService.create(availabilityDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(false, "La disponibilidad ha sido creada exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<AvailabilityDTO>> get(@PathVariable String id) throws Exception {
        AvailabilityDTO availabilityDTO = availabilityService.get(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, availabilityDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> delete(@PathVariable String id) throws Exception {
        availabilityService.delete(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "La disponibilidad ha sido eliminada"));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<AvailabilityDTO>>> listAll() {
        List<AvailabilityDTO> list = availabilityService.listAll();
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @GetMapping("/accommodation/{accommodationId}")
    public ResponseEntity<ResponseDTO<List<AvailabilityDTO>>> listByAccommodation(@PathVariable String accommodationId) throws Exception {
        List<AvailabilityDTO> list = availabilityService.listByAccommodation(accommodationId);
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @GetMapping("/accommodation/{accommodationId}/available")
    public ResponseEntity<ResponseDTO<List<AvailabilityDTO>>> listAvailableDates(@PathVariable String accommodationId) throws Exception {
        List<AvailabilityDTO> list = availabilityService.listAvailableDates(accommodationId);
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @GetMapping("/accommodation/{accommodationId}/unavailable")
    public ResponseEntity<ResponseDTO<List<AvailabilityDTO>>> listUnavailableDates(@PathVariable String accommodationId) throws Exception {
        List<AvailabilityDTO> list = availabilityService.listUnavailableDates(accommodationId);
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @GetMapping("/accommodation/{accommodationId}/range")
    public ResponseEntity<ResponseDTO<List<AvailabilityDTO>>> listByDateRange(
            @PathVariable String accommodationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws Exception {
        List<AvailabilityDTO> list = availabilityService.listByDateRange(accommodationId, startDate, endDate);
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> edit(@PathVariable String id, @Valid @RequestBody EditAvailabilityDTO availabilityDTO) throws Exception {
        availabilityService.edit(id, availabilityDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, "La disponibilidad ha sido actualizada"));
    }

    @GetMapping("/accommodation/{accommodationId}/date/{date}/check")
    public ResponseEntity<ResponseDTO<Boolean>> isDateAvailable(
            @PathVariable String accommodationId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws Exception {
        boolean available = availabilityService.isDateAvailable(accommodationId, date);
        return ResponseEntity.ok(new ResponseDTO<>(false, available));
    }

    @PutMapping("/accommodation/{accommodationId}/date/{date}/block")
    public ResponseEntity<ResponseDTO<String>> blockDate(
            @PathVariable String accommodationId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String reason) throws Exception {
        availabilityService.blockDate(accommodationId, date, reason);
        return ResponseEntity.ok(new ResponseDTO<>(false, "La fecha ha sido bloqueada"));
    }

    @PutMapping("/accommodation/{accommodationId}/date/{date}/unblock")
    public ResponseEntity<ResponseDTO<String>> unblockDate(
            @PathVariable String accommodationId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws Exception {
        availabilityService.unblockDate(accommodationId, date);
        return ResponseEntity.ok(new ResponseDTO<>(false, "La fecha ha sido desbloqueada"));
    }

    @GetMapping("/accommodation/{accommodationId}/stats")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getStats(@PathVariable String accommodationId) throws Exception {
        Map<String, Object> stats = new HashMap<>();
        Long availableCount = availabilityService.countAvailableDates(accommodationId);
        Long unavailableCount = availabilityService.countUnavailableDates(accommodationId);
        
        stats.put("accommodation_id", accommodationId);
        stats.put("available_dates", availableCount);
        stats.put("unavailable_dates", unavailableCount);
        stats.put("total_dates", availableCount + unavailableCount);
        
        return ResponseEntity.ok(new ResponseDTO<>(false, stats));
    }
}