package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.create.CreateUserDTO;
import co.edu.uniquindio.application.dto.edit.EditUserDTO;
import co.edu.uniquindio.application.models.enums.Role;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.security.JWTUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PRUEBAS DE INTEGRACIÓN del UserController
 *
 * ¿Qué prueban estas pruebas?
 * - Endpoints HTTP completos (POST, GET, PUT, DELETE)
 * - Serialización/Deserialización de JSON
 * - Validaciones de entrada
 * - Códigos de respuesta HTTP
 * - Autenticación y autorización (JWT)
 * - Manejo de errores
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;  // ✅ Simula peticiones HTTP

    @Autowired
    private ObjectMapper objectMapper;  // ✅ Convierte objetos a JSON

    @Autowired
    private UserRepository userRepository;  // ✅ Verifica datos en BD

    @Autowired
    private JWTUtils jwtUtils;  // ✅ Genera tokens JWT para pruebas

    // ========================================
    // PRUEBAS DE REGISTRO (POST /api/auth/register)
    // ========================================

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("POST /api/auth/register - Crear usuario exitosamente")
    void testCreateUser_Success() throws Exception {
        // ========== ARRANGE ==========
        CreateUserDTO userDTO = new CreateUserDTO(
                "Carlos Nuevo",
                "3001234567",
                "carlos.nuevo@email.com",
                "Password123",
                "http://photo.url",
                LocalDate.of(1990, 1, 1),
                Role.GUEST
        );

        // ========== ACT & ASSERT ==========
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andDo(print())  // ✅ Imprime detalles en consola
                .andExpect(status().isCreated())  // ✅ Espera 201 Created
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").value("Usuario registrado exitosamente"));

        // Verificar que el usuario se guardó en la BD
        var savedUser = userRepository.findByEmail("carlos.nuevo@email.com");
        assert savedUser.isPresent();
        assert savedUser.get().getName().equals("Carlos Nuevo");
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("POST /api/auth/register - Falla con email duplicado")
    void testCreateUser_EmailAlreadyExists() throws Exception {
        // ========== ARRANGE ==========
        CreateUserDTO userDTO = new CreateUserDTO(
                "Usuario Duplicado",
                "3009876543",
                "maria.gomez@example.com",  // ✅ Email que ya existe
                "Password123",
                "http://photo.url",
                LocalDate.of(1995, 5, 15),
                Role.GUEST
        );

        // ========== ACT & ASSERT ==========
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andDo(print())
                .andExpect(status().isConflict())  // ✅ Espera 409 Conflict
                .andExpect(jsonPath("$.error").value(true))
                .andExpect(jsonPath("$.content").value(containsString("ya está en uso")));
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("POST /api/auth/register - Falla con datos inválidos")
    void testCreateUser_InvalidData() throws Exception {
        // ========== ARRANGE ==========
        CreateUserDTO userDTO = new CreateUserDTO(
                "",  // ❌ Nombre vacío
                "3001234567",
                "email-invalido",  // ❌ Email inválido
                "123",  // ❌ Contraseña muy corta
                "",
                LocalDate.of(2030, 1, 1),  // ❌ Fecha futura
                Role.GUEST
        );

        // ========== ACT & ASSERT ==========
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())  // ✅ Espera 400 Bad Request
                .andExpect(jsonPath("$.error").value(true))
                .andExpect(jsonPath("$.content").isArray());
    }

    // ========================================
    // PRUEBAS DE LOGIN (POST /api/auth/login)
    // ========================================

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("POST /api/auth/login - Login exitoso devuelve token")
    void testLogin_Success() throws Exception {
        // ========== ARRANGE ==========
        var loginDTO = Map.of(
                "email", "maria.gomez@example.com",
                "password", "Password123"
        );

        // ========== ACT & ASSERT ==========
        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content.token").exists())
                .andExpect(jsonPath("$.content.token").isString());
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("POST /api/auth/login - Falla con contraseña incorrecta")
    void testLogin_WrongPassword() throws Exception {
        // ========== ARRANGE ==========
        var loginDTO = Map.of(
                "email", "maria.gomez@example.com",
                "password", "WrongPassword123"
        );

        // ========== ACT & ASSERT ==========
        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andDo(print())
                .andExpect(status().isInternalServerError())  // Puede variar según tu implementación
                .andExpect(jsonPath("$.error").value(true));
    }

    // ========================================
    // PRUEBAS DE OBTENER USUARIO (GET /api/users/{id})
    // ========================================

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("GET /api/users/{id} - Obtener usuario exitosamente")
    void testGetUser_Success() throws Exception {
        // ========== ARRANGE ==========
        String userId = "u001";  // María Gómez
        String token = jwtUtils.generateToken(userId, Map.of("role", "ROLE_GUEST"));

        // ========== ACT & ASSERT ==========
        mockMvc.perform(get("/api/users/" + userId)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content.id").value(userId))
                .andExpect(jsonPath("$.content.name").value("María Gómez"))
                .andExpect(jsonPath("$.content.email").value("maria.gomez@example.com"))
                .andExpect(jsonPath("$.content.role").value("GUEST"));
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("GET /api/users/{id} - Falla con usuario no encontrado")
    void testGetUser_NotFound() throws Exception {
        // ========== ARRANGE ==========
        String userId = "u999999";
        String token = jwtUtils.generateToken("u001", Map.of("role", "ROLE_GUEST"));

        // ========== ACT & ASSERT ==========
        mockMvc.perform(get("/api/users/" + userId)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isNotFound())  // ✅ Espera 404
                .andExpect(jsonPath("$.error").value(true))
                .andExpect(jsonPath("$.content").value(containsString("no encontrado")));
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("GET /api/users/{id} - Falla sin token de autenticación")
    void testGetUser_Unauthorized() throws Exception {
        // ========== ARRANGE ==========
        String userId = "u001";

        // ========== ACT & ASSERT ==========
        // ❌ NO enviar header de Authorization
        mockMvc.perform(get("/api/users/" + userId))
                .andDo(print())
                .andExpect(status().isUnauthorized());  // ✅ Espera 401
    }

    // ========================================
    // PRUEBAS DE LISTAR USUARIOS (GET /api/users)
    // ========================================

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("GET /api/users - Listar todos los usuarios (ADMIN)")
    void testListAllUsers_Success() throws Exception {
        // ========== ARRANGE ==========
        String adminToken = jwtUtils.generateToken("u003", Map.of("role", "ROLE_ADMIN"));

        // ========== ACT & ASSERT ==========
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(greaterThan(0)));
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("GET /api/users - Falla sin permisos de ADMIN")
    void testListAllUsers_Forbidden() throws Exception {
        // ========== ARRANGE ==========
        String guestToken = jwtUtils.generateToken("u001", Map.of("role", "ROLE_GUEST"));

        // ========== ACT & ASSERT ==========
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + guestToken))
                .andDo(print())
                .andExpect(status().isForbidden());  // ✅ Espera 403
    }

    // ========================================
    // PRUEBAS DE ACTUALIZAR USUARIO (PUT /api/users/{id})
    // ========================================

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("PUT /api/users/{id} - Actualizar usuario exitosamente")
    void testUpdateUser_Success() throws Exception {
        // ========== ARRANGE ==========
        String userId = "u001";
        String token = jwtUtils.generateToken(userId, Map.of("role", "ROLE_GUEST"));

        EditUserDTO editDTO = new EditUserDTO(
                "María Gómez Actualizada",
                "3009999999",
                "http://nueva-foto.url",
                LocalDate.of(1990, 6, 15),
                Role.GUEST
        );

        // ========== ACT & ASSERT ==========
        mockMvc.perform(put("/api/users/" + userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(editDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").value(containsString("actualizado")));

        // Verificar cambios en BD
        var updatedUser = userRepository.findById(userId).orElseThrow();
        assert updatedUser.getName().equals("María Gómez Actualizada");
        assert updatedUser.getPhone().equals("3009999999");
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("PUT /api/users/{id} - Falla al editar perfil de otro usuario")
    void testUpdateUser_Forbidden() throws Exception {
        // ========== ARRANGE ==========
        String userId = "u001";  // María
        String otherUserToken = jwtUtils.generateToken("u002", Map.of("role", "ROLE_GUEST"));  // Juan

        EditUserDTO editDTO = new EditUserDTO(
                "Intento Edición",
                "3001111111",
                "",
                LocalDate.of(1990, 1, 1),
                Role.GUEST
        );

        // ========== ACT & ASSERT ==========
        mockMvc.perform(put("/api/users/" + userId)
                        .header("Authorization", "Bearer " + otherUserToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(editDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())  // Puede variar según implementación
                .andExpect(jsonPath("$.error").value(true));
    }

    // ========================================
    // PRUEBAS DE ELIMINAR USUARIO (DELETE /api/users/{id})
    // ========================================

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("DELETE /api/users/{id} - Eliminar usuario exitosamente")
    void testDeleteUser_Success() throws Exception {
        // ========== ARRANGE ==========
        String userId = "u001";
        String token = jwtUtils.generateToken(userId, Map.of("role", "ROLE_GUEST"));

        // ========== ACT & ASSERT ==========
        mockMvc.perform(delete("/api/users/" + userId)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false));

        // Verificar eliminación en BD
        var deletedUser = userRepository.findById(userId);
        assert deletedUser.isEmpty();
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("DELETE /api/users/{id} - Falla al eliminar cuenta de otro usuario")
    void testDeleteUser_Forbidden() throws Exception {
        // ========== ARRANGE ==========
        String userId = "u001";  // María
        String otherUserToken = jwtUtils.generateToken("u002", Map.of("role", "ROLE_GUEST"));  // Juan

        // ========== ACT & ASSERT ==========
        mockMvc.perform(delete("/api/users/" + userId)
                        .header("Authorization", "Bearer " + otherUserToken))
                .andDo(print())
                .andExpect(status().isBadRequest())  // Puede variar según implementación
                .andExpect(jsonPath("$.error").value(true));
    }

    // ========================================
    // PRUEBAS DE ACTIVAR/DESACTIVAR USUARIO
    // ========================================

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("PUT /api/users/{id}/activate - Activar usuario (ADMIN)")
    void testActivateUser_Success() throws Exception {
        // ========== ARRANGE ==========
        String userId = "u004";  // Usuario INACTIVE
        String adminToken = jwtUtils.generateToken("u003", Map.of("role", "ROLE_ADMIN"));

        // ========== ACT & ASSERT ==========
        mockMvc.perform(put("/api/users/" + userId + "/activate")
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false));

        // Verificar cambio en BD
        var activatedUser = userRepository.findById(userId).orElseThrow();
        assert activatedUser.getStatus().toString().equals("ACTIVE");
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("PUT /api/users/{id}/deactivate - Desactivar usuario (ADMIN)")
    void testDeactivateUser_Success() throws Exception {
        // ========== ARRANGE ==========
        String userId = "u001";
        String adminToken = jwtUtils.generateToken("u003", Map.of("role", "ROLE_ADMIN"));

        // ========== ACT & ASSERT ==========
        mockMvc.perform(put("/api/users/" + userId + "/deactivate")
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false));

        // Verificar cambio en BD
        var deactivatedUser = userRepository.findById(userId).orElseThrow();
        assert deactivatedUser.getStatus().toString().equals("INACTIVE");
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("PUT /api/users/{id}/activate - Falla sin permisos ADMIN")
    void testActivateUser_Forbidden() throws Exception {
        // ========== ARRANGE ==========
        String userId = "u004";
        String guestToken = jwtUtils.generateToken("u001", Map.of("role", "ROLE_GUEST"));

        // ========== ACT & ASSERT ==========
        mockMvc.perform(put("/api/users/" + userId + "/activate")
                        .header("Authorization", "Bearer " + guestToken))
                .andDo(print())
                .andExpect(status().isForbidden());  // ✅ Espera 403
    }

    // ========================================
    // PRUEBAS DE AUTENTICACION (EJERCICIO 2)
    // ========================================

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("POST /api/users/authenticate - Autenticación exitosa")
    void testAuthenticate_Success() throws Exception {
        // ========== ARRANGE ==========
        String email = "maria.gomez@example.com";
        String password = "Password123";

        // ========== ACT & ASSERT ==========
        mockMvc.perform(post("/api/users/authenticate")
                        .param("email", email)
                        .param("password", password))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content.email").value(email))
                .andExpect(jsonPath("$.content.name").value("María Gómez"));
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("POST /api/users/authenticate - Falla con contraseña incorrecta")
    void testAuthenticate_WrongPassword() throws Exception {
        // ========== ARRANGE ==========
        String email = "maria.gomez@example.com";
        String wrongPassword = "WrongPassword";

        // ========== ACT & ASSERT ==========
        mockMvc.perform(post("/api/users/authenticate")
                        .param("email", email)
                        .param("password", wrongPassword))
                .andDo(print())
                .andExpect(status().isBadRequest())  // Puede variar según implementación
                .andExpect(jsonPath("$.error").value(true));
    }
}