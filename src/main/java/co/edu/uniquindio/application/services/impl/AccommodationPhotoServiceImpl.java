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
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
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

        // Calcular el orden de visualización
        Long photoCount = photoRepository.countByAccommodationId(accommodationId);
        Integer displayOrder = photoCount != null ? photoCount.intValue() : 0;

        // Crear registro de foto
        AccommodationPhoto newPhoto = AccommodationPhoto.builder()
                .id(UUID.randomUUID().toString())
                .imageUrl(imageUrl)
                .isPrimary(isPrimary)
                .accommodation(accommodation.get())
                .displayOrder(displayOrder)
                .build();

        photoRepository.save(newPhoto);
        return imageUrl;
    }

    @Override
    @Transactional
    public void deletePhoto(String photoId) throws Exception {
        Optional<AccommodationPhoto> photo = photoRepository.findById(photoId);
        if (photo.isEmpty()) {
            throw new NotFoundException("Foto no encontrada");
        }

        // Extraer public_id de la URL para eliminar de Cloudinary
        String imageUrl = photo.get().getImageUrl();
        String publicId = extractPublicId(imageUrl);
        
        // Solo intentar eliminar de Cloudinary si se pudo extraer el publicId
        if (publicId != null && !publicId.isEmpty()) {
            try {
                imageService.delete(publicId);
            } catch (Exception e) {
                System.err.println("⚠️ Error al eliminar imagen de Cloudinary: " + e.getMessage());
                // Continuar con la eliminación de la base de datos aunque falle Cloudinary
            }
        }

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
    @Transactional
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
        if (currentPrimary != null && !currentPrimary.getId().equals(photoId)) {
            currentPrimary.setIsPrimary(false);
            photoRepository.save(currentPrimary);
        }

        // Marcar nueva foto como primaria
        targetPhoto.setIsPrimary(true);
        photoRepository.save(targetPhoto);
    }

    /**
     * Extrae el public_id de una URL de Cloudinary de forma segura
     * 
     * Ejemplos de URLs de Cloudinary:
     * - https://res.cloudinary.com/demo/image/upload/v1234567/folder/image.jpg
     * - https://res.cloudinary.com/demo/image/upload/folder/image.jpg
     * - https://res.cloudinary.com/demo/raw/upload/v1234567/document.pdf
     * 
     * @param imageUrl URL completa de la imagen en Cloudinary
     * @return public_id extraído o cadena vacía si no se pudo extraer
     */
    private String extractPublicId(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return "";
        }

        try {
            // Buscar la parte después de /upload/
            String[] parts = imageUrl.split("/upload/");
            if (parts.length < 2) {
                System.err.println("⚠️ URL no tiene formato esperado de Cloudinary: " + imageUrl);
                return "";
            }

            String pathAfterUpload = parts[1];

            // Remover versión si existe (v1234567/)
            if (pathAfterUpload.matches("^v\\d+/.*")) {
                int firstSlash = pathAfterUpload.indexOf('/');
                if (firstSlash > 0 && firstSlash < pathAfterUpload.length() - 1) {
                    pathAfterUpload = pathAfterUpload.substring(firstSlash + 1);
                }
            }

            // Remover la extensión del archivo
            int lastDotIndex = pathAfterUpload.lastIndexOf('.');
            if (lastDotIndex > 0) {
                pathAfterUpload = pathAfterUpload.substring(0, lastDotIndex);
            }

            // Validar que el resultado no esté vacío
            if (pathAfterUpload.isEmpty()) {
                System.err.println("⚠️ No se pudo extraer public_id de: " + imageUrl);
                return "";
            }

            return pathAfterUpload;

        } catch (Exception e) {
            System.err.println("❌ Error al extraer public_id de URL: " + imageUrl);
            System.err.println("   Error: " + e.getMessage());
            return "";
        }
    }
}