package co.edu.uniquindio.application.services;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface AccommodationPhotoService {
    String uploadPhoto(String accommodationId, MultipartFile photo, boolean isPrimary) throws Exception;
    void deletePhoto(String photoId) throws Exception;
    List<String> getPhotosByAccommodation(String accommodationId) throws Exception;
    void setPrimaryPhoto(String photoId) throws Exception;
}