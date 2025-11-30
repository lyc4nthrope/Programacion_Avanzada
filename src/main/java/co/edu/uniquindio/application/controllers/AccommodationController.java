package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.AccommodationDTO;
import co.edu.uniquindio.application.dto.create.CreateAccommodationDTO;
import co.edu.uniquindio.application.dto.edit.EditAccommodationDTO;
import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.models.enums.AccommodationStatus;
import co.edu.uniquindio.application.services.AccommodationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accommodations")
@RequiredArgsConstructor
public class AccommodationController {

    private final AccommodationService accommodationService;

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody CreateAccommodationDTO accommodationDTO) throws Exception {
        String hostId = "host-1";
        accommodationService.create(accommodationDTO, hostId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(false, "El alojamiento ha sido creado exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> edit(@PathVariable String id, @Valid @RequestBody EditAccommodationDTO accommodationDTO) throws Exception {
        accommodationService.edit(id, accommodationDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El alojamiento ha sido actualizado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> delete(@PathVariable String id) throws Exception {
        accommodationService.delete(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El alojamiento ha sido eliminado"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<AccommodationDTO>> get(@PathVariable String id) throws Exception {
        AccommodationDTO accommodationDTO = accommodationService.get(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, accommodationDTO));
    }

    // ✅ ENDPOINT SIN PAGINACIÓN (lista simple)
    @GetMapping
    public ResponseEntity<ResponseDTO<List<AccommodationDTO>>> listAll(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) AccommodationStatus status
    ) throws Exception {
        List<AccommodationDTO> list;

        if (status != null) {
            list = accommodationService.listByStatus(status);
        } else if (city != null && !city.isEmpty()) {
            list = accommodationService.listByCity(city);
        } else if (minPrice != null && maxPrice != null) {
            list = accommodationService.listByPriceRange(minPrice, maxPrice);
        } else {
            list = accommodationService.listAll();
        }

        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    // ✅ NUEVO: ENDPOINT CON PAGINACIÓN (para consultas grandes)
    @GetMapping("/paginated")
    public ResponseEntity<ResponseDTO<Page<AccommodationDTO>>> listAllPaginated(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) AccommodationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) throws Exception {
        
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? 
                    Sort.by(sortBy).descending() : 
                    Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AccommodationDTO> resultPage;

        if (status != null) {
            resultPage = accommodationService.listByStatusPaginated(status, pageable);
        } else if (city != null && !city.isEmpty()) {
            resultPage = accommodationService.listByCityPaginated(city, pageable);
        } else if (minPrice != null && maxPrice != null) {
            resultPage = accommodationService.listByPriceRangePaginated(minPrice, maxPrice, pageable);
        } else {
            resultPage = accommodationService.listAllPaginated(pageable);
        }

        return ResponseEntity.ok(new ResponseDTO<>(false, resultPage));
    }

    @GetMapping("/active")
    public ResponseEntity<ResponseDTO<List<AccommodationDTO>>> listActive() {
        List<AccommodationDTO> list = accommodationService.listActive();
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<ResponseDTO<String>> activate(@PathVariable String id) throws Exception {
        accommodationService.activate(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El alojamiento ha sido activado"));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ResponseDTO<String>> deactivate(@PathVariable String id) throws Exception {
        accommodationService.deactivate(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El alojamiento ha sido desactivado"));
    }

    @DeleteMapping("/{id}/soft")
    public ResponseEntity<ResponseDTO<String>> softDelete(@PathVariable String id) throws Exception {
        accommodationService.softDelete(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El alojamiento ha sido marcado como eliminado"));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ResponseDTO<String>> changeStatus(
            @PathVariable String id,
            @RequestParam AccommodationStatus status) throws Exception {
        accommodationService.changeStatus(id, status);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El estado del alojamiento ha sido cambiado a: " + status));
    }

    // ✅ EJERCICIO 1: Consulta por ciudad con paginación
    @GetMapping("/by-city-paginated")
    public ResponseEntity<ResponseDTO<Page<AccommodationDTO>>> listByCityPaginated(
            @RequestParam String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) throws Exception {
        
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? 
                    Sort.by(sortBy).descending() : 
                    Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AccommodationDTO> resultPage = accommodationService.listByCityPaginated(city, pageable);
        
        return ResponseEntity.ok(new ResponseDTO<>(false, resultPage));
    }

    // ✅ EJERCICIO 4: Búsqueda por texto con paginación
    @GetMapping("/search")
    public ResponseEntity<ResponseDTO<Page<AccommodationDTO>>> searchByName(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws Exception {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AccommodationDTO> resultPage = accommodationService.searchByNamePaginated(text, pageable);
        
        return ResponseEntity.ok(new ResponseDTO<>(false, resultPage));
    }

    // ✅ EJERCICIO 5: Consultas personalizadas

    @GetMapping("/active-by-city-price")
    public ResponseEntity<ResponseDTO<Page<AccommodationDTO>>> findActiveByCityAndMaxPrice(
            @RequestParam String city,
            @RequestParam Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AccommodationDTO> resultPage = accommodationService.findActiveByCityAndMaxPrice(city, maxPrice, pageable);
        
        return ResponseEntity.ok(new ResponseDTO<>(false, resultPage));
    }

    @GetMapping("/by-capacity-rating")
    public ResponseEntity<ResponseDTO<Page<AccommodationDTO>>> findByCapacityAndRating(
            @RequestParam Integer minCapacity,
            @RequestParam Double minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AccommodationDTO> resultPage = accommodationService.findByCapacityAndRating(minCapacity, minRating, pageable);
        
        return ResponseEntity.ok(new ResponseDTO<>(false, resultPage));
    }

    @GetMapping("/most-popular")
    public ResponseEntity<ResponseDTO<Page<AccommodationDTO>>> findMostPopular(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AccommodationDTO> resultPage = accommodationService.findMostPopular(pageable);
        
        return ResponseEntity.ok(new ResponseDTO<>(false, resultPage));
    }

    @GetMapping("/by-price-range-active")
    public ResponseEntity<ResponseDTO<Page<AccommodationDTO>>> findByPriceRangeActive(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("pricePerNight").ascending());
        Page<AccommodationDTO> resultPage = accommodationService.findByPriceRangeActive(minPrice, maxPrice, pageable);
        
        return ResponseEntity.ok(new ResponseDTO<>(false, resultPage));
    }

    @GetMapping("/near-location")
    public ResponseEntity<ResponseDTO<Page<AccommodationDTO>>> findNearLocation(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10") Double radiusKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AccommodationDTO> resultPage = accommodationService.findNearLocation(latitude, longitude, radiusKm, pageable);
        
        return ResponseEntity.ok(new ResponseDTO<>(false, resultPage));
    }
}