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

    // Gestión de status
    void activateUser(String id) throws Exception;
    void deactivateUser(String id) throws Exception;
    void softDeleteUser(String id) throws Exception;
    List<UserDTO> listByStatus(UserStatus status);
    List<UserDTO> listActiveUsers();
    boolean emailExists(String email);
    UserDTO getByEmail(String email) throws Exception;

    // ✅ EJERCICIO 2: Autenticación de usuario
    /**
     * Autentica un usuario verificando email y contraseña
     * @param email Email del usuario
     * @param password Contraseña sin encriptar
     * @return UserDTO si la autenticación es exitosa
     * @throws Exception Si las credenciales son inválidas
     */
    UserDTO authenticate(String email, String password) throws Exception;
}