package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.AccommodationDTO;
import co.edu.uniquindio.application.dto.create.CreateAccommodationDTO;
import co.edu.uniquindio.application.dto.edit.EditAccommodationDTO;
import java.util.List;

public interface AccommodationService {

    void create(CreateAccommodationDTO accommodationDTO, String hostId) throws Exception;

    AccommodationDTO get(String id) throws Exception;

    void delete(String id) throws Exception;

    List<AccommodationDTO> listAll();

    List<AccommodationDTO> listByCity(String city) throws Exception;

    List<AccommodationDTO> listByPriceRange(Double minPrice, Double maxPrice) throws Exception;

    void edit(String id, EditAccommodationDTO accommodationDTO) throws Exception;
}