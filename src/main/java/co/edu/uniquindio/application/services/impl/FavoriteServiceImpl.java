package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.create.CreateFavoriteDTO;
import co.edu.uniquindio.application.dto.FavoriteDTO;
import co.edu.uniquindio.application.exceptions.InvalidOperationException;
import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.mappers.FavoriteMapper;
import co.edu.uniquindio.application.models.entitys.Accommodation;
import co.edu.uniquindio.application.models.entitys.Favorite;
import co.edu.uniquindio.application.models.entitys.User;
import co.edu.uniquindio.application.repositories.AccommodationRepository;
import co.edu.uniquindio.application.repositories.FavoriteRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteMapper favoriteMapper;
    private final UserRepository userRepository;
    private final AccommodationRepository accommodationRepository;

    @Override
    public void addFavorite(CreateFavoriteDTO favoriteDTO) throws Exception {
        // Validar que el usuario existe
        Optional<User> user = userRepository.findById(favoriteDTO.userId());
        if (user.isEmpty()) {
            throw new NotFoundException("El usuario con ID '" + favoriteDTO.userId() + "' no fue encontrado.");
        }

        // Validar que el alojamiento existe
        Optional<Accommodation> accommodation = accommodationRepository.findById(favoriteDTO.accommodationId());
        if (accommodation.isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + favoriteDTO.accommodationId() + "' no fue encontrado.");
        }

        // Validar que no sea un favorito duplicado
        Optional<Favorite> existingFavorite = favoriteRepository.findByUserIdAndAccommodationId(
                favoriteDTO.userId(),
                favoriteDTO.accommodationId()
        );
        if (existingFavorite.isPresent()) {
            throw new InvalidOperationException("Este alojamiento ya está en los favoritos del usuario.");
        }

        // Crear el favorito
        Favorite newFavorite = favoriteMapper.toEntity(favoriteDTO);
        newFavorite.setUser(user.get());
        newFavorite.setAccommodation(accommodation.get());

        // Guardar el favorito
        favoriteRepository.save(newFavorite);
    }

    @Override
    public FavoriteDTO get(String id) throws Exception {
        Optional<Favorite> favoriteOptional = favoriteRepository.findById(id);

        if (favoriteOptional.isEmpty()) {
            throw new NotFoundException("El favorito con ID '" + id + "' no fue encontrado.");
        }

        return favoriteMapper.toFavoriteDTO(favoriteOptional.get());
    }

    @Override
    public void removeFavorite(String id) throws Exception {
        Optional<Favorite> favoriteOptional = favoriteRepository.findById(id);

        if (favoriteOptional.isEmpty()) {
            throw new NotFoundException("El favorito con ID '" + id + "' no fue encontrado.");
        }

        favoriteRepository.deleteById(id);
    }

    @Override
    public void removeFavoriteByUserAndAccommodation(String userId, String accommodationId) throws Exception {
        Optional<Favorite> favoriteOptional = favoriteRepository.findByUserIdAndAccommodationId(userId, accommodationId);

        if (favoriteOptional.isEmpty()) {
            throw new NotFoundException("Este alojamiento no está en los favoritos del usuario.");
        }

        favoriteRepository.delete(favoriteOptional.get());
    }

    @Override
    public List<FavoriteDTO> listAll() {
        return favoriteRepository.findAll()
                .stream()
                .map(favoriteMapper::toFavoriteDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FavoriteDTO> listByUser(String userId) throws Exception {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("El usuario con ID '" + userId + "' no fue encontrado.");
        }

        return favoriteRepository.findByUserId(userId)
                .stream()
                .map(favoriteMapper::toFavoriteDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FavoriteDTO> listByAccommodation(String accommodationId) throws Exception {
        if (accommodationRepository.findById(accommodationId).isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + accommodationId + "' no fue encontrado.");
        }

        return favoriteRepository.findByAccommodationId(accommodationId)
                .stream()
                .map(favoriteMapper::toFavoriteDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isFavorite(String userId, String accommodationId) throws Exception {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("El usuario con ID '" + userId + "' no fue encontrado.");
        }

        if (accommodationRepository.findById(accommodationId).isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + accommodationId + "' no fue encontrado.");
        }

        return favoriteRepository.findByUserIdAndAccommodationId(userId, accommodationId).isPresent();
    }

    @Override
    public Long countFavoritesByUser(String userId) throws Exception {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("El usuario con ID '" + userId + "' no fue encontrado.");
        }

        return favoriteRepository.countByUserId(userId);
    }

    @Override
    public Long countFavoritesByAccommodation(String accommodationId) throws Exception {
        if (accommodationRepository.findById(accommodationId).isEmpty()) {
            throw new NotFoundException("El alojamiento con ID '" + accommodationId + "' no fue encontrado.");
        }

        return favoriteRepository.countByAccommodationId(accommodationId);
    }
}