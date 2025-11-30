package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.PasswordResetCodeDTO;

public interface PasswordResetCodeService {

    /**
     * Genera un código de recuperación de contraseña para un email dado.
     * Envía el código por correo electrónico al usuario.
     * 
     * @param email Email del usuario que solicita la recuperación
     * @throws Exception Si el email no existe o hay un error al enviar el email
     */
    void generateResetCode(String email) throws Exception;

    /**
     * Valida un código de recuperación y cambia la contraseña del usuario.
     * 
     * @param code Código de verificación de 6 dígitos
     * @param newPassword Nueva contraseña del usuario
     * @throws Exception Si el código es inválido, expiró o la contraseña no cumple requisitos
     */
    void validateAndResetPassword(String code, String newPassword) throws Exception;

    /**
     * Obtiene información de un código de recuperación por su código.
     * 
     * @param code Código de recuperación
     * @return PasswordResetCodeDTO con la información del código
     * @throws Exception Si el código no existe
     */
    PasswordResetCodeDTO getByCode(String code) throws Exception;

    /**
     * Verifica si un código es válido (existe, no ha sido usado y no ha expirado).
     * 
     * @param code Código a verificar
     * @return true si el código es válido, false en caso contrario
     */
    boolean isCodeValid(String code);

    /**
     * Elimina todos los códigos expirados de la base de datos.
     * Este método debe ejecutarse periódicamente (ej. con @Scheduled).
     */
    void cleanupExpiredCodes();

    /**
     * Invalida todos los códigos activos de un usuario.
     * Útil cuando el usuario solicita un nuevo código.
     * 
     * @param userId ID del usuario
     * @throws Exception Si el usuario no existe
     */
    void invalidateUserCodes(String userId) throws Exception;
}