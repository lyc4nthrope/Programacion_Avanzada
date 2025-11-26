package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.AccommodationDTO;
import co.edu.uniquindio.application.dto.CreateAccommodationDTO;
import co.edu.uniquindio.application.dto.EditAccommodationDTO;
import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.services.AccommodationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        // Por ahora, usamos un hostId de ejemplo. Esto debe venir del usuario autenticado
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

    @GetMapping
    public ResponseEntity<ResponseDTO<List<AccommodationDTO>>> listAll(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) throws Exception {
        List<AccommodationDTO> list;

        // Filtrar por ciudad si se proporciona
        if (city != null && !city.isEmpty()) {
            list = accommodationService.listByCity(city);
        }
        // Filtrar por rango de precio si se proporciona
        else if (minPrice != null && maxPrice != null) {
            list = accommodationService.listByPriceRange(minPrice, maxPrice);
        }
        // Listar todos si no hay filtros
        else {
            list = accommodationService.listAll();
        }

        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }
}