package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.PasswordResetCodeDTO;
import co.edu.uniquindio.application.dto.RequestPasswordResetDTO;
import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.dto.ValidatePasswordResetCodeDTO;
import co.edu.uniquindio.application.services.PasswordResetCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password-reset")
@RequiredArgsConstructor
public class PasswordResetCodeController {

    private final PasswordResetCodeService passwordResetCodeService;

    /**
     * Endpoint para solicitar un código de recuperación de contraseña
     * POST /api/password-reset/request
     */
    @PostMapping("/request")
    public ResponseEntity<ResponseDTO<String>> requestPasswordReset(
            @Valid @RequestBody RequestPasswordResetDTO requestDTO) throws Exception {
        
        passwordResetCodeService.generateResetCode(requestDTO.email());
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDTO<>(false, 
                        "Se ha enviado un código de recuperación a tu correo electrónico. " +
                        "El código expirará en 15 minutos."));
    }

    /**
     * Endpoint para validar el código y cambiar la contraseña
     * POST /api/password-reset/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<ResponseDTO<String>> validateAndResetPassword(
            @Valid @RequestBody ValidatePasswordResetCodeDTO validateDTO) throws Exception {
        
        passwordResetCodeService.validateAndResetPassword(
                validateDTO.code(), 
                validateDTO.newPassword()
        );
        
        return ResponseEntity.ok(new ResponseDTO<>(false, 
                "Tu contraseña ha sido cambiada exitosamente. " +
                "Ya puedes iniciar sesión con tu nueva contraseña."));
    }

    /**
     * Endpoint para verificar si un código es válido
     * GET /api/password-reset/check/{code}
     */
    @GetMapping("/check/{code}")
    public ResponseEntity<ResponseDTO<Boolean>> checkCodeValidity(@PathVariable String code) {
        boolean isValid = passwordResetCodeService.isCodeValid(code);
        
        String message = isValid 
                ? "El código es válido" 
                : "El código es inválido, ha expirado o ya fue usado";
        
        return ResponseEntity.ok(new ResponseDTO<>(false, isValid));
    }

    /**
     * Endpoint para obtener información de un código (solo para debug/admin)
     * GET /api/password-reset/info/{code}
     */
    @GetMapping("/info/{code}")
    public ResponseEntity<ResponseDTO<PasswordResetCodeDTO>> getCodeInfo(
            @PathVariable String code) throws Exception {
        
        PasswordResetCodeDTO codeInfo = passwordResetCodeService.getByCode(code);
        return ResponseEntity.ok(new ResponseDTO<>(false, codeInfo));
    }

    /**
     * Endpoint para invalidar todos los códigos de un usuario
     * POST /api/password-reset/invalidate/{userId}
     */
    @PostMapping("/invalidate/{userId}")
    public ResponseEntity<ResponseDTO<String>> invalidateUserCodes(
            @PathVariable String userId) throws Exception {
        
        passwordResetCodeService.invalidateUserCodes(userId);
        
        return ResponseEntity.ok(new ResponseDTO<>(false, 
                "Todos los códigos activos del usuario han sido invalidados."));
    }
}