package co.edu.uniquindio.application.services;

import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

public interface ImageService {
    Map upload(MultipartFile image) throws Exception;
    Map delete(String imageId) throws Exception;
}
