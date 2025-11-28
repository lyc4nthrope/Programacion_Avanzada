package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.create.CreateReviewDTO;
import co.edu.uniquindio.application.dto.edit.EditReviewDTO;
import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.dto.ReviewDTO;
import co.edu.uniquindio.application.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody CreateReviewDTO reviewDTO) throws Exception {
        reviewService.create(reviewDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(false, "La reseña ha sido creada exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<ReviewDTO>> get(@PathVariable Long id) throws Exception {
        ReviewDTO reviewDTO = reviewService.get(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, reviewDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> delete(@PathVariable Long id) throws Exception {
        reviewService.delete(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "La reseña ha sido eliminada"));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<ReviewDTO>>> listAll() {
        List<ReviewDTO> list = reviewService.listAll();
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @GetMapping("/accommodation/{accommodationId}")
    public ResponseEntity<ResponseDTO<List<ReviewDTO>>> listByAccommodation(@PathVariable String accommodationId) throws Exception {
        List<ReviewDTO> list = reviewService.listByAccommodation(accommodationId);
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseDTO<List<ReviewDTO>>> listByUser(@PathVariable String userId) throws Exception {
        List<ReviewDTO> list = reviewService.listByUser(userId);
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> edit(@PathVariable Long id, @Valid @RequestBody EditReviewDTO reviewDTO) throws Exception {
        reviewService.edit(id, reviewDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, "La reseña ha sido actualizada"));
    }

    @GetMapping("/accommodation/{accommodationId}/average")
    public ResponseEntity<ResponseDTO<Double>> getAverageRating(@PathVariable String accommodationId) throws Exception {
        Double average = reviewService.getAverageRating(accommodationId);
        return ResponseEntity.ok(new ResponseDTO<>(false, average));
    }
}