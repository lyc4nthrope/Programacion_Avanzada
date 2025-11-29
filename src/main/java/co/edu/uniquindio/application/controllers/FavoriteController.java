package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.create.CreateFavoriteDTO;
import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.dto.FavoriteDTO;
import co.edu.uniquindio.application.services.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> addFavorite(@Valid @RequestBody CreateFavoriteDTO favoriteDTO) throws Exception {
        favoriteService.addFavorite(favoriteDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(false, "El alojamiento ha sido agregado a favoritos"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<FavoriteDTO>> get(@PathVariable String id) throws Exception {
        FavoriteDTO favoriteDTO = favoriteService.get(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, favoriteDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> removeFavorite(@PathVariable String id) throws Exception {
        favoriteService.removeFavorite(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El alojamiento ha sido removido de favoritos"));
    }

    @DeleteMapping("/user/{userId}/accommodation/{accommodationId}")
    public ResponseEntity<ResponseDTO<String>> removeFavoriteByUserAndAccommodation(
            @PathVariable String userId,
            @PathVariable String accommodationId) throws Exception {
        favoriteService.removeFavoriteByUserAndAccommodation(userId, accommodationId);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El alojamiento ha sido removido de favoritos"));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<FavoriteDTO>>> listAll() {
        List<FavoriteDTO> list = favoriteService.listAll();
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseDTO<List<FavoriteDTO>>> listByUser(@PathVariable String userId) throws Exception {
        List<FavoriteDTO> list = favoriteService.listByUser(userId);
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @GetMapping("/accommodation/{accommodationId}")
    public ResponseEntity<ResponseDTO<List<FavoriteDTO>>> listByAccommodation(@PathVariable String accommodationId) throws Exception {
        List<FavoriteDTO> list = favoriteService.listByAccommodation(accommodationId);
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @GetMapping("/user/{userId}/accommodation/{accommodationId}/check")
    public ResponseEntity<ResponseDTO<Boolean>> isFavorite(
            @PathVariable String userId,
            @PathVariable String accommodationId) throws Exception {
        boolean isFavorite = favoriteService.isFavorite(userId, accommodationId);
        return ResponseEntity.ok(new ResponseDTO<>(false, isFavorite));
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<ResponseDTO<Long>> countFavoritesByUser(@PathVariable String userId) throws Exception {
        Long count = favoriteService.countFavoritesByUser(userId);
        return ResponseEntity.ok(new ResponseDTO<>(false, count));
    }

    @GetMapping("/accommodation/{accommodationId}/count")
    public ResponseEntity<ResponseDTO<Long>> countFavoritesByAccommodation(@PathVariable String accommodationId) throws Exception {
        Long count = favoriteService.countFavoritesByAccommodation(accommodationId);
        return ResponseEntity.ok(new ResponseDTO<>(false, count));
    }

    @GetMapping("/stats")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getStats(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String accommodationId) throws Exception {
        
        Map<String, Object> stats = new HashMap<>();

        if (userId != null && !userId.isEmpty()) {
            stats.put("user_id", userId);
            stats.put("favorite_count", favoriteService.countFavoritesByUser(userId));
        }

        if (accommodationId != null && !accommodationId.isEmpty()) {
            stats.put("accommodation_id", accommodationId);
            stats.put("favorite_count", favoriteService.countFavoritesByAccommodation(accommodationId));
        }

        return ResponseEntity.ok(new ResponseDTO<>(false, stats));
    }
}