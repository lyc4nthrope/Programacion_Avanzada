package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.models.entitys.User;
import co.edu.uniquindio.application.models.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * ✅ EJERCICIO 2: Autenticación de usuario
     * Busca un usuario por email (usaremos esto junto con verificación de contraseña en el servicio)
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca usuarios por estado
     */
    List<User> findByStatus(UserStatus status);

    /**
     * Busca usuarios activos
     */
    List<User> findByStatusEquals(UserStatus status);

    /**
     * Verifica si existe un email
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuarios por rol
     */
    List<User> findByRole(co.edu.uniquindio.application.models.enums.Role role);

    /**
     * Cuenta usuarios por estado
     */
    Long countByStatus(UserStatus status);
}