package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.services.AccommodationPhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/accommodation-photos")
@RequiredArgsConstructor
public class AccommodationPhotoController {

    private final AccommodationPhotoService accommodationPhotoService;

    @PostMapping(value = "/upload/{accommodationId}", consumes = "multipart/form-data")
    public ResponseEntity<ResponseDTO<String>> uploadPhoto(
            @PathVariable String accommodationId,
            @RequestParam("photo") MultipartFile photo,
            @RequestParam(defaultValue = "false") boolean isPrimary) throws Exception {
        
        String imageUrl = accommodationPhotoService.uploadPhoto(accommodationId, photo, isPrimary);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(false, imageUrl));
    }

    @GetMapping("/{accommodationId}")
    public ResponseEntity<ResponseDTO<List<String>>> getPhotosByAccommodation(
            @PathVariable String accommodationId) throws Exception {
        
        List<String> photos = accommodationPhotoService.getPhotosByAccommodation(accommodationId);
        return ResponseEntity.ok(new ResponseDTO<>(false, photos));
    }

    @PutMapping("/{photoId}/set-primary")
    public ResponseEntity<ResponseDTO<String>> setPrimaryPhoto(@PathVariable String photoId) throws Exception {
        accommodationPhotoService.setPrimaryPhoto(photoId);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Foto establecida como principal"));
    }

    @DeleteMapping("/{photoId}")
    public ResponseEntity<ResponseDTO<String>> deletePhoto(@PathVariable String photoId) throws Exception {
        accommodationPhotoService.deletePhoto(photoId);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Foto eliminada exitosamente"));
    }
}