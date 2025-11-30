package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.create.CreateUserDTO;
import co.edu.uniquindio.application.dto.edit.EditUserDTO;
import co.edu.uniquindio.application.dto.UserDTO;
import co.edu.uniquindio.application.models.enums.UserStatus;
import java.util.List;

public interface UserService {

    void create(CreateUserDTO userDTO) throws Exception;

    UserDTO get(String id) throws Exception;

    void delete(String id) throws Exception;

    List<UserDTO> listAll();

    void edit(String id, EditUserDTO userDTO) throws Exception;

    // ✅ NUEVOS MÉTODOS PARA GESTIONAR STATUS

    /**
     * Activa un usuario (cambia status a ACTIVE)
     */
    void activateUser(String id) throws Exception;

    /**
     * Desactiva un usuario (cambia status a INACTIVE)
     */
    void deactivateUser(String id) throws Exception;

    /**
     * Marca un usuario como eliminado (soft delete - cambia status a DELETED)
     */
    void softDeleteUser(String id) throws Exception;

    /**
     * Lista usuarios por estado
     */
    List<UserDTO> listByStatus(UserStatus status);

    /**
     * Lista solo usuarios activos
     */
    List<UserDTO> listActiveUsers();

    /**
     * Verifica si un email ya está registrado
     */
    boolean emailExists(String email);

    /**
     * Obtiene un usuario por email
     */
    UserDTO getByEmail(String email) throws Exception;
}