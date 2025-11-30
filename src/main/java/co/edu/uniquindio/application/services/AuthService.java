package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.LoginDTO;
import co.edu.uniquindio.application.dto.TokenDTO;
import co.edu.uniquindio.application.dto.create.CreateUserDTO;

public interface AuthService {
    
    /**
     * Registra un nuevo usuario en el sistema
     */
    void register(CreateUserDTO userDTO) throws Exception;
    
    /**
     * Autentica un usuario y retorna un token JWT
     */
    TokenDTO login(LoginDTO loginDTO) throws Exception;
    
    /**
     * Obtiene el ID del usuario autenticado desde el contexto de seguridad
     */
    String getAuthenticatedUserId();
}