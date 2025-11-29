package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.models.entitys.HostProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HostProfileRepository extends JpaRepository<HostProfile, String> {

    // Buscar perfil de anfitrión por usuario
    Optional<HostProfile> findByUserId(String userId);

    // Verificar si un usuario tiene perfil de anfitrión
    boolean existsByUserId(String userId);

}