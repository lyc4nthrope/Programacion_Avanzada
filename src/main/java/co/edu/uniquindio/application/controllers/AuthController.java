package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.LoginDTO;
import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.dto.TokenDTO;
import co.edu.uniquindio.application.dto.create.CreateUserDTO;
import co.edu.uniquindio.application.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint para registrar un nuevo usuario
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<String>> register(
            @Valid @RequestBody CreateUserDTO userDTO) throws Exception {
        
        authService.register(userDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(false, "Usuario registrado exitosamente"));
    }

    /**
     * Endpoint para hacer login
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<TokenDTO>> login(
            @Valid @RequestBody LoginDTO loginDTO) throws Exception {
        
        TokenDTO token = authService.login(loginDTO);
        
        return ResponseEntity.ok(new ResponseDTO<>(false, token));
    }

    /**
     * Endpoint de prueba para verificar autenticación
     * GET /api/auth/test
     * Requiere token válido
     */
    @GetMapping("/test")
    public ResponseEntity<ResponseDTO<String>> test() {
        String userId = authService.getAuthenticatedUserId();
        return ResponseEntity.ok(
            new ResponseDTO<>(false, "Autenticado correctamente. Tu ID es: " + userId)
        );
    }
}