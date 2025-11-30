package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.EmailDTO;
import co.edu.uniquindio.application.dto.PasswordResetCodeDTO;
import co.edu.uniquindio.application.exceptions.InvalidOperationException;
import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.mappers.PasswordResetCodeMapper;
import co.edu.uniquindio.application.models.entitys.PasswordResetCode;
import co.edu.uniquindio.application.models.entitys.User;
import co.edu.uniquindio.application.repositories.PasswordResetCodeRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.EmailService;
import co.edu.uniquindio.application.services.PasswordResetCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetCodeServiceImpl implements PasswordResetCodeService {

    private final PasswordResetCodeRepository passwordResetCodeRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordResetCodeMapper passwordResetCodeMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRATION_MINUTES = 15;

    @Override
    @Transactional
    public void generateResetCode(String email) throws Exception {
        // Buscar usuario por email
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("No existe un usuario con el email: " + email);
        }

        User user = userOptional.get();

        // Invalidar códigos anteriores del usuario
        invalidateUserCodes(user.getId());

        // Generar código de 6 dígitos
        String code = generateRandomCode();

        // Crear el registro del código
        PasswordResetCode resetCode = PasswordResetCode.builder()
                .id(UUID.randomUUID().toString())
                .code(code)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES))
                .used(false)
                .build();

        // Guardar en la base de datos
        passwordResetCodeRepository.save(resetCode);

        // Enviar email con el código
        sendResetCodeEmail(user.getEmail(), user.getName(), code);
    }

    @Override
    @Transactional
    public void validateAndResetPassword(String code, String newPassword) throws Exception {
        // Validar que el código existe
        Optional<PasswordResetCode> resetCodeOptional = passwordResetCodeRepository.findByCode(code);
        if (resetCodeOptional.isEmpty()) {
            throw new NotFoundException("El código de recuperación es inválido.");
        }

        PasswordResetCode resetCode = resetCodeOptional.get();

        // Verificar que no ha sido usado
        if (resetCode.getUsed()) {
            throw new InvalidOperationException("Este código ya ha sido utilizado.");
        }

        // Verificar que no ha expirado
        if (resetCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidOperationException("El código de recuperación ha expirado. Solicita uno nuevo.");
        }

        // Validar la nueva contraseña
        validatePassword(newPassword);

        // Obtener el usuario
        User user = resetCode.getUser();

        // Cambiar la contraseña
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Marcar el código como usado
        resetCode.setUsed(true);
        passwordResetCodeRepository.save(resetCode);

        // Enviar email de confirmación
        sendPasswordChangedEmail(user.getEmail(), user.getName());
    }

    @Override
    public PasswordResetCodeDTO getByCode(String code) throws Exception {
        Optional<PasswordResetCode> resetCodeOptional = passwordResetCodeRepository.findByCode(code);
        
        if (resetCodeOptional.isEmpty()) {
            throw new NotFoundException("El código de recuperación no existe.");
        }

        return passwordResetCodeMapper.toDTO(resetCodeOptional.get());
    }

    @Override
    public boolean isCodeValid(String code) {
        try {
            Optional<PasswordResetCode> resetCodeOptional = 
                passwordResetCodeRepository.findValidCode(code, LocalDateTime.now());
            return resetCodeOptional.isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 * * * *") // Ejecutar cada hora
    public void cleanupExpiredCodes() {
        try {
            passwordResetCodeRepository.deleteExpiredCodes(LocalDateTime.now());
            System.out.println("🧹 Códigos expirados eliminados: " + LocalDateTime.now());
        } catch (Exception e) {
            System.err.println("❌ Error al limpiar códigos expirados: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void invalidateUserCodes(String userId) throws Exception {
        // Validar que el usuario existe
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Usuario no encontrado.");
        }

        // Obtener todos los códigos no usados del usuario
        List<PasswordResetCode> unusedCodes = passwordResetCodeRepository.findUnusedCodesByUser(userId);

        // Marcar todos como usados
        for (PasswordResetCode code : unusedCodes) {
            code.setUsed(true);
            passwordResetCodeRepository.save(code);
        }
    }

    /**
     * Genera un código aleatorio de 6 dígitos
     */
    private String generateRandomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Código entre 100000 y 999999
        return String.valueOf(code);
    }

    /**
     * Valida que la contraseña cumpla con los requisitos mínimos
     */
    private void validatePassword(String password) throws InvalidOperationException {
        if (password == null || password.length() < 7) {
            throw new InvalidOperationException("La contraseña debe tener al menos 7 caracteres.");
        }

        if (password.length() > 20) {
            throw new InvalidOperationException("La contraseña no puede tener más de 20 caracteres.");
        }

        // Validar que contenga al menos una letra mayúscula
        if (!password.matches(".*[A-Z].*")) {
            throw new InvalidOperationException("La contraseña debe contener al menos una letra mayúscula.");
        }

        // Validar que contenga al menos una letra minúscula
        if (!password.matches(".*[a-z].*")) {
            throw new InvalidOperationException("La contraseña debe contener al menos una letra minúscula.");
        }

        // Validar que contenga al menos un número
        if (!password.matches(".*[0-9].*")) {
            throw new InvalidOperationException("La contraseña debe contener al menos un número.");
        }
    }

    /**
     * Envía el email con el código de recuperación
     */
    private void sendResetCodeEmail(String email, String userName, String code) {
        String subject = "Código de Recuperación de Contraseña";
        String body = String.format(
            """
            Hola %s,
            
            Has solicitado recuperar tu contraseña. Tu código de verificación es:
            
            %s
            
            Este código expirará en %d minutos.
            
            Si no solicitaste este código, puedes ignorar este mensaje.
            
            Saludos,
            El equipo de Alojamientos
            """,
            userName,
            code,
            CODE_EXPIRATION_MINUTES
        );

        EmailDTO emailDTO = new EmailDTO(subject, body, email);
        
        try {
            emailService.sendMail(emailDTO);
            System.out.println("✅ Código de recuperación enviado a: " + email);
        } catch (Exception e) {
            System.err.println("❌ Error al enviar email de recuperación: " + e.getMessage());
        }
    }

    /**
     * Envía el email de confirmación de cambio de contraseña
     */
    private void sendPasswordChangedEmail(String email, String userName) {
        String subject = "Contraseña Cambiada Exitosamente";
        String body = String.format(
            """
            Hola %s,
            
            Tu contraseña ha sido cambiada exitosamente.
            
            Si no realizaste este cambio, contacta inmediatamente con soporte.
            
            Saludos,
            El equipo de Alojamientos
            """,
            userName
        );

        EmailDTO emailDTO = new EmailDTO(subject, body, email);
        
        try {
            emailService.sendMail(emailDTO);
            System.out.println("✅ Confirmación de cambio de contraseña enviada a: " + email);
        } catch (Exception e) {
            System.err.println("❌ Error al enviar email de confirmación: " + e.getMessage());
        }
    }
}