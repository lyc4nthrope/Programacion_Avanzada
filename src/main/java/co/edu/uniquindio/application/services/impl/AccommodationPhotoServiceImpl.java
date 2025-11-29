package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.models.entitys.Accommodation;
import co.edu.uniquindio.application.models.entitys.AccommodationPhoto;
import co.edu.uniquindio.application.repositories.AccommodationPhotoRepository;
import co.edu.uniquindio.application.repositories.AccommodationRepository;
import co.edu.uniquindio.application.services.AccommodationPhotoService;
import co.edu.uniquindio.application.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccommodationPhotoServiceImpl implements AccommodationPhotoService {

    private final AccommodationPhotoRepository photoRepository;
    private final AccommodationRepository accommodationRepository;
    private final ImageService imageService;

    @Override
    public String uploadPhoto(String accommodationId, MultipartFile photo, boolean isPrimary) throws Exception {
        // Validar que el alojamiento existe
        Optional<Accommodation> accommodation = accommodationRepository.findById(accommodationId);
        if (accommodation.isEmpty()) {
            throw new NotFoundException("Alojamiento no encontrado");
        }

        // Subir imagen a Cloudinary
        Map uploadResult = imageService.upload(photo);
        String imageUrl = uploadResult.get("url").toString();

        // Si es primaria, desmarcar otras fotos primarias
        if (isPrimary) {
            AccommodationPhoto currentPrimary = photoRepository
                    .findByAccommodationIdAndIsPrimaryTrue(accommodationId);
            if (currentPrimary != null) {
                currentPrimary.setIsPrimary(false);
                photoRepository.save(currentPrimary);
            }
        }

        // Crear registro de foto
        AccommodationPhoto newPhoto = AccommodationPhoto.builder()
                .id(UUID.randomUUID().toString())
                .imageUrl(imageUrl)
                .isPrimary(isPrimary)
                .accommodation(accommodation.get())
                .displayOrder(photoRepository.countByAccommodationId(accommodationId).intValue())
                .build();

        photoRepository.save(newPhoto);
        return imageUrl;
    }

    @Override
    public void deletePhoto(String photoId) throws Exception {
        Optional<AccommodationPhoto> photo = photoRepository.findById(photoId);
        if (photo.isEmpty()) {
            throw new NotFoundException("Foto no encontrada");
        }

        // Eliminar de Cloudinary (extraer public_id de la URL)
        String imageUrl = photo.get().getImageUrl();
        String publicId = extractPublicId(imageUrl);
        imageService.delete(publicId);

        // Eliminar de base de datos
        photoRepository.deleteById(photoId);
    }

    @Override
    public List<String> getPhotosByAccommodation(String accommodationId) throws Exception {
        return photoRepository.findByAccommodationIdOrderByDisplayOrderAsc(accommodationId)
                .stream()
                .map(AccommodationPhoto::getImageUrl)
                .collect(Collectors.toList());
    }

    @Override
    public void setPrimaryPhoto(String photoId) throws Exception {
        Optional<AccommodationPhoto> photo = photoRepository.findById(photoId);
        if (photo.isEmpty()) {
            throw new NotFoundException("Foto no encontrada");
        }

        AccommodationPhoto targetPhoto = photo.get();
        String accommodationId = targetPhoto.getAccommodation().getId();

        // Desmarcar foto primaria actual
        AccommodationPhoto currentPrimary = photoRepository
                .findByAccommodationIdAndIsPrimaryTrue(accommodationId);
        if (currentPrimary != null) {
            currentPrimary.setIsPrimary(false);
            photoRepository.save(currentPrimary);
        }

        // Marcar nueva foto como primaria
        targetPhoto.setIsPrimary(true);
        photoRepository.save(targetPhoto);
    }

    private String extractPublicId(String imageUrl) {
        // Extraer el public_id de la URL de Cloudinary
        // Ejemplo: https://res.cloudinary.com/.../upload/v123/folder/image.jpg -> folder/image
        String[] parts = imageUrl.split("/upload/");
        if (parts.length > 1) {
            String path = parts[1];
            return path.substring(path.indexOf("/") + 1, path.lastIndexOf("."));
        }
        return "";
    }
}