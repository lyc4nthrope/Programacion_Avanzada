package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.models.entitys.AccommodationPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccommodationPhotoRepository extends JpaRepository<AccommodationPhoto, String> {

    // Buscar fotos por alojamiento
    List<AccommodationPhoto> findByAccommodationId(String accommodationId);

    // Buscar foto principal de un alojamiento
    AccommodationPhoto findByAccommodationIdAndIsPrimaryTrue(String accommodationId);

    // Buscar fotos ordenadas por displayOrder
    List<AccommodationPhoto> findByAccommodationIdOrderByDisplayOrderAsc(String accommodationId);

    // Contar fotos de un alojamiento
    Long countByAccommodationId(String accommodationId);
}