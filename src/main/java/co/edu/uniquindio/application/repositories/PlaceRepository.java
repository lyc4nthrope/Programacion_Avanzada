package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.models.entitys.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Accommodation, Long>{
}
