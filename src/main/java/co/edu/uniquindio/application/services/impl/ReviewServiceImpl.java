package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.create.CreateReviewDTO;
import co.edu.uniquindio.application.dto.edit.EditReviewDTO;
import co.edu.uniquindio.application.dto.ReviewDTO;
import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.mappers.ReviewMapper;
import co.edu.uniquindio.application.models.entitys.Accommodation;
import co.edu.uniquindio.application.models.entitys.Review;
import co.edu.uniquindio.application.models.entitys.User;
import co.edu.uniquindio.application.repositories.AccommodationRepository;
import co.edu.uniquindio.application.repositories.ReviewRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final AccommodationRepository accommodationRepository;
    private final UserRepository userRepository;

    @Override
    public void create(CreateReviewDTO reviewDTO) throws Exception {
        // Validar que el alojamiento existe
        Optional<Accommodation> accommodation = accommodationRepository.findById(reviewDTO.accommodationId());
        if (accommodation.isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + reviewDTO.accommodationId() + "' no fue encontrado.");
        }

        // Validar que el usuario existe
        Optional<User> user = userRepository.findById(reviewDTO.userId());
        if (user.isEmpty()) {
            throw new NotFoundException("El usuario con ID '" + reviewDTO.userId() + "' no fue encontrado.");
        }

        // Crear la reseña
        Review newReview = reviewMapper.toEntity(reviewDTO);
        newReview.setAccommodation(accommodation.get());
        newReview.setUser(user.get());

        // Guardar la reseña
        reviewRepository.save(newReview);

        // Actualizar calificación promedio del alojamiento
        updateAccommodationRating(reviewDTO.accommodationId());
    }

    @Override
    public ReviewDTO get(Long id) throws Exception {
        Optional<Review> reviewOptional = reviewRepository.findById(id);

        if (reviewOptional.isEmpty()) {
            throw new NotFoundException("La reseña con ID '" + id + "' no fue encontrada.");
        }

        return reviewMapper.toReviewDTO(reviewOptional.get());
    }

    @Override
    public void delete(Long id) throws Exception {
        Optional<Review> reviewOptional = reviewRepository.findById(id);

        if (reviewOptional.isEmpty()) {
            throw new NotFoundException("La reseña con ID '" + id + "' no fue encontrada.");
        }

        Review review = reviewOptional.get();
        String accommodationId = review.getAccommodation().getId();

        // Eliminar reseña
        reviewRepository.deleteById(id);

        // Actualizar calificación promedio
        updateAccommodationRating(accommodationId);
    }

    @Override
    public List<ReviewDTO> listAll() {
        return reviewRepository.findAll()
                .stream()
                .map(reviewMapper::toReviewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDTO> listByAccommodation(String accommodationId) throws Exception {
        // Validar que el alojamiento existe
        if (accommodationRepository.findById(accommodationId).isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + accommodationId + "' no fue encontrado.");
        }

        return reviewRepository.findByAccommodationId(accommodationId)
                .stream()
                .map(reviewMapper::toReviewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDTO> listByUser(String userId) throws Exception {
        // Validar que el usuario existe
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("El usuario con ID '" + userId + "' no fue encontrado.");
        }

        return reviewRepository.findByUserId(userId)
                .stream()
                .map(reviewMapper::toReviewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void edit(Long id, EditReviewDTO reviewDTO) throws Exception {
        Optional<Review> reviewOptional = reviewRepository.findById(id);

        if (reviewOptional.isEmpty()) {
            throw new NotFoundException("La reseña con ID '" + id + "' no fue encontrada.");
        }

        Review review = reviewOptional.get();
        String accommodationId = review.getAccommodation().getId();

        // Actualizar campos
        review.setComment(reviewDTO.comment());
        review.setRating(reviewDTO.rating());

        // Guardar cambios
        reviewRepository.save(review);

        // Actualizar calificación promedio
        updateAccommodationRating(accommodationId);
    }

    @Override
    public Double getAverageRating(String accommodationId) throws Exception {
        // Validar que el alojamiento existe
        if (accommodationRepository.findById(accommodationId).isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + accommodationId + "' no fue encontrado.");
        }

        List<Review> reviews = reviewRepository.findByAccommodationId(accommodationId);

        if (reviews.isEmpty()) {
            return 0.0;
        }

        return reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }

    //actualizar calificación promedio
    private void updateAccommodationRating(String accommodationId) throws NotFoundException {
        Optional<Accommodation> accommodation = accommodationRepository.findById(accommodationId);

        if (accommodation.isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + accommodationId + "' no fue encontrado.");
        }

        Accommodation accom = accommodation.get();
        List<Review> reviews = reviewRepository.findByAccommodationId(accommodationId);

        if (reviews.isEmpty()) {
            accom.setAverageRating(0.0);
            accom.setRatingCount(0);
        } else {
            double avgRating = reviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);
            accom.setAverageRating(avgRating);
            accom.setRatingCount(reviews.size());
        }

        accommodationRepository.save(accom);
    }
}