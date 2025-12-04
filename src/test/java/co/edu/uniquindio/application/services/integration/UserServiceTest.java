package co.edu.uniquindio.application.services.integration;

import co.edu.uniquindio.application.dto.UserDTO;
import co.edu.uniquindio.application.dto.create.CreateUserDTO;
import co.edu.uniquindio.application.exceptions.InvalidOperationException;
import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.exceptions.ValueConflictException;
import co.edu.uniquindio.application.models.enums.Role;
import co.edu.uniquindio.application.models.enums.UserStatus;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.AuthService;
import co.edu.uniquindio.application.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * PRUEBAS DE INTEGRACIÓN de UserService
 *
 * ¿Qué son las pruebas de integración?
 * Prueban la interacción entre MÚLTIPLES componentes
 * Usan base de datos REAL (MariaDB con datos de prueba)
 * Usan el contexto completo de Spring (@SpringBootTest)
 * Son MÁS LENTAS que las unitarias (segundos)
 * Verifican que tod el flujo funcione correctamente
 */
@SpringBootTest
@Transactional  // ✅ Revierte cambios después de cada prueba
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // ✅ Mock solo para AuthService (evita problemas de seguridad en pruebas)
    @MockitoBean
    private AuthService authService;

    // ========================================
    // PRUEBAS DEL METODO create()
    // ========================================

    @Test
    @Sql("classpath:dataset.sql")  // ✅ Carga datos de prueba ANTES de ejecutar
    @DisplayName("Crear usuario exitosamente cuando el email NO existe")
    void testCreateUser_Success() {
        // ========== ARRANGE ==========
        CreateUserDTO userDTO = new CreateUserDTO(
                "Carlos Martínez",
                "3001234567",
                "carlos.nuevo@email.com",  // ✅ Email que NO existe en dataset.sql
                "Password123",
                "http://photo.url",
                LocalDate.of(1990, 1, 1),
                Role.GUEST
        );

        // ========== ACT & ASSERT ==========
        // Verificar que NO se lance ninguna excepción
        assertDoesNotThrow(() -> userService.create(userDTO));

        // Verificar que el usuario SE GUARDÓ en la base de datos
        var savedUser = userRepository.findByEmail("carlos.nuevo@email.com");
        assertTrue(savedUser.isPresent());
        assertEquals("Carlos Martínez", savedUser.get().getName());
        assertEquals(UserStatus.ACTIVE, savedUser.get().getStatus());
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("Crear usuario lanza excepción cuando el email YA EXISTE")
    void testCreateUser_EmailAlreadyExists() {
        // ========== ARRANGE ==========
        // Email que YA EXISTE en dataset.sql
        CreateUserDTO userDTO = new CreateUserDTO(
                "Usuario Duplicado",
                "3009876543",
                "maria.gomez@example.com",  // ✅ Ya existe en dataset.sql
                "Password123",
                "http://photo.url",
                LocalDate.of(1995, 5, 15),
                Role.GUEST
        );

        // ========== ACT & ASSERT ==========
        ValueConflictException exception = assertThrows(
                ValueConflictException.class,
                () -> userService.create(userDTO)
        );

        assertEquals("El correo electrónico ya está en uso.", exception.getMessage());
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("Crear usuario con rol ADMIN lanza excepción")
    void testCreateUser_AdminRoleNotAllowed() {
        // ========== ARRANGE ==========
        CreateUserDTO userDTO = new CreateUserDTO(
                "Intento Admin",
                "3001112233",
                "admin.nuevo@email.com",
                "Password123",
                "",
                LocalDate.of(1990, 1, 1),
                Role.ADMIN  // ❌ No se permite crear ADMIN públicamente
        );

        // ========== ACT & ASSERT ==========
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> userService.create(userDTO)
        );

        assertTrue(exception.getMessage().contains("ADMIN"));
    }

    // ========================================
    // PRUEBAS DEL METODO get()
    // ========================================

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("Obtener usuario exitosamente cuando existe")
    void testGetUser_Success() throws Exception {
        // ========== ARRANGE ==========
        // Usuario que existe en dataset.sql
        String userId = "u001";  // María Gómez

        // ========== ACT ==========
        UserDTO result = userService.get(userId);

        // ========== ASSERT ==========
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("María Gómez", result.name());
        assertEquals("maria.gomez@example.com", result.email());
        assertEquals(Role.GUEST, result.role());
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("Obtener usuario lanza excepción cuando NO existe")
    void testGetUser_NotFound() {
        // ========== ARRANGE ==========
        String nonExistentId = "u999999";

        // ========== ACT & ASSERT ==========
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.get(nonExistentId)
        );

        assertTrue(exception.getMessage().contains(nonExistentId));
    }

    // ========================================
    // PRUEBAS DEL METODO delete()
    // ========================================

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("Eliminar usuario exitosamente cuando es el propietario")
    void testDeleteUser_Success() throws Exception {
        // ========== ARRANGE ==========
        String userId = "u001";  // María Gómez

        // Simular que el usuario autenticado es el mismo que se quiere eliminar
        when(authService.getAuthenticatedUserId()).thenReturn(userId);

        // ========== ACT ==========
        assertDoesNotThrow(() -> userService.delete(userId));

        // ========== ASSERT ==========
        // Verificar que el usuario YA NO EXISTE en la base de datos
        var deletedUser = userRepository.findById(userId);
        assertFalse(deletedUser.isPresent());
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("Eliminar usuario lanza excepción cuando NO es el propietario")
    void testDeleteUser_NotOwner() {
        // ========== ARRANGE ==========
        String userId = "u001";  // María Gómez
        String authenticatedUserId = "u002";  // Juan Pérez (otro usuario)

        // ✅ Simular que el usuario autenticado es DIFERENTE
        when(authService.getAuthenticatedUserId()).thenReturn(authenticatedUserId);

        // ========== ACT & ASSERT ==========
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> userService.delete(userId)
        );

        assertTrue(exception.getMessage().contains("tu propia cuenta"));
    }

    // ========================================
    // PRUEBAS DEL METODO edit()
    // ========================================

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("Editar usuario exitosamente cuando es el propietario")
    void testEditUser_Success() throws Exception {
        // ========== ARRANGE ==========
        String userId = "u001";  // María Gómez

        // Simular que el usuario autenticado es el mismo
        when(authService.getAuthenticatedUserId()).thenReturn(userId);

        var editDTO = new co.edu.uniquindio.application.dto.edit.EditUserDTO(
                "María Gómez Actualizada",
                "3009999999",
                "http://nueva-foto.url",
                LocalDate.of(1990, 6, 15),
                Role.GUEST
        );

        // ========== ACT ==========
        assertDoesNotThrow(() -> userService.edit(userId, editDTO));

        // ========== ASSERT ==========
        // Verificar que los cambios se guardaron
        var updatedUser = userRepository.findById(userId).orElseThrow();
        assertEquals("María Gómez Actualizada", updatedUser.getName());
        assertEquals("3009999999", updatedUser.getPhone());
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("Editar usuario lanza excepción cuando NO es el propietario")
    void testEditUser_NotOwner() {
        // ========== ARRANGE ==========
        String userId = "u001";  // María
        String authenticatedUserId = "u002";  // Juan (otro usuario)

        when(authService.getAuthenticatedUserId()).thenReturn(authenticatedUserId);

        var editDTO = new co.edu.uniquindio.application.dto.edit.EditUserDTO(
                "Intento Edición",
                "3001111111",
                "",
                LocalDate.of(1990, 1, 1),
                Role.GUEST
        );

        // ========== ACT & ASSERT ==========
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> userService.edit(userId, editDTO)
        );

        assertTrue(exception.getMessage().contains("tu propio perfil"));
    }

    // ========================================
    // PRUEBAS DE GESTION DE ESTADO
    // ========================================

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("Activar usuario INACTIVE exitosamente")
    void testActivateUser_Success() throws Exception {
        // ========== ARRANGE ==========
        String userId = "u004";  // Usuario INACTIVE en dataset.sql

        // ========== ACT ==========
        assertDoesNotThrow(() -> userService.activateUser(userId));

        // ========== ASSERT ==========
        var activatedUser = userRepository.findById(userId).orElseThrow();
        assertEquals(UserStatus.ACTIVE, activatedUser.getStatus());
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("Activar usuario DELETED lanza excepción")
    void testActivateUser_DeletedUser() throws Exception {
        // ========== ARRANGE ==========
        String userId = "u001";

        // Primero hacer soft delete
        userService.softDeleteUser(userId);

        // ========== ACT & ASSERT ==========
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> userService.activateUser(userId)
        );

        assertTrue(exception.getMessage().contains("eliminado"));
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("Desactivar usuario exitosamente")
    void testDeactivateUser_Success() throws Exception {
        // ========== ARRANGE ==========
        String userId = "u001";  // Usuario ACTIVE

        // ========== ACT ==========
        assertDoesNotThrow(() -> userService.deactivateUser(userId));

        // ========== ASSERT ==========
        var deactivatedUser = userRepository.findById(userId).orElseThrow();
        assertEquals(UserStatus.INACTIVE, deactivatedUser.getStatus());
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("Soft delete usuario exitosamente")
    void testSoftDeleteUser_Success() throws Exception {
        // ========== ARRANGE ==========
        String userId = "u001";

        // ========== ACT ==========
        assertDoesNotThrow(() -> userService.softDeleteUser(userId));

        // ========== ASSERT ==========
        var deletedUser = userRepository.findById(userId).orElseThrow();
        assertEquals(UserStatus.DELETED, deletedUser.getStatus());
    }

    // ========================================
    // PRUEBAS DEL METODO authenticate()
    // ========================================

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("Autenticar usuario con credenciales correctas")
    void testAuthenticate_Success() throws Exception {
        // ========== ARRANGE ==========
        // Credenciales del dataset.sql (contraseña: "Password123")
        String email = "maria.gomez@example.com";
        String password = "Password123";

        // ========== ACT ==========
        UserDTO result = userService.authenticate(email, password);

        // ========== ASSERT ==========
        assertNotNull(result);
        assertEquals(email, result.email());
        assertEquals("María Gómez", result.name());
        assertEquals(Role.GUEST, result.role());
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("Autenticar con contraseña incorrecta lanza excepción")
    void testAuthenticate_WrongPassword() {
        // ========== ARRANGE ==========
        String email = "maria.gomez@example.com";
        String wrongPassword = "WrongPassword123";

        // ========== ACT & ASSERT ==========
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> userService.authenticate(email, wrongPassword)
        );

        assertTrue(exception.getMessage().contains("Credenciales inválidas"));
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("Autenticar usuario INACTIVE lanza excepción")
    void testAuthenticate_InactiveUser() {
        // ========== ARRANGE ==========
        String email = "inactive@example.com";  // Usuario u004 (INACTIVE)
        String password = "Password123";

        // ========== ACT & ASSERT ==========
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> userService.authenticate(email, password)
        );

        assertTrue(exception.getMessage().contains("no está activo"));
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("Autenticar con email inexistente lanza excepción")
    void testAuthenticate_UserNotFound() {
        // ========== ARRANGE ==========
        String email = "noexiste@email.com";
        String password = "Password123";

        // ========== ACT & ASSERT ==========
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.authenticate(email, password)
        );

        assertTrue(exception.getMessage().contains("Credenciales inválidas"));
    }

    // ========================================
    // PRUEBAS DE BUSQUEDA
    // ========================================

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("Buscar usuario por email exitosamente")
    void testGetByEmail_Success() throws Exception {
        // ========== ARRANGE ==========
        String email = "juan.perez@example.com";

        // ========== ACT ==========
        UserDTO result = userService.getByEmail(email);

        // ========== ASSERT ==========
        assertNotNull(result);
        assertEquals(email, result.email());
        assertEquals("Juan Pérez", result.name());
        assertEquals(Role.HOST, result.role());
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("Buscar usuario por email inexistente lanza excepción")
    void testGetByEmail_NotFound() {
        // ========== ARRANGE ==========
        String email = "noexiste@email.com";

        // ========== ACT & ASSERT ==========
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getByEmail(email)
        );

        assertTrue(exception.getMessage().contains(email));
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("Verificar que email existe devuelve true")
    void testEmailExists_True() {
        // ========== ARRANGE ==========
        String email = "maria.gomez@example.com";

        // ========== ACT ==========
        boolean exists = userService.emailExists(email);

        // ========== ASSERT ==========
        assertTrue(exists);
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("Verificar que email NO existe devuelve false")
    void testEmailExists_False() {
        // ========== ARRANGE ==========
        String email = "noexiste@email.com";

        // ========== ACT ==========
        boolean exists = userService.emailExists(email);

        // ========== ASSERT ==========
        assertFalse(exists);
    }

    // ========================================
    // PRUEBAS DE LISTADO
    // ========================================

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("Listar todos los usuarios activos")
    void testListActiveUsers() {
        // ========== ACT ==========
        var activeUsers = userService.listActiveUsers();

        // ========== ASSERT ==========
        assertNotNull(activeUsers);
        assertFalse(activeUsers.isEmpty());

        // Verificar que TODOS son ACTIVE
        activeUsers.forEach(user -> {
            var userEntity = userRepository.findById(user.id()).orElseThrow();
            assertEquals(UserStatus.ACTIVE, userEntity.getStatus());
        });
    }

    @Test
    @Sql("classpath:dataset.sql")
    @DisplayName("Listar usuarios por estado INACTIVE")
    void testListByStatus_Inactive() {
        // ========== ACT ==========
        var inactiveUsers = userService.listByStatus(UserStatus.INACTIVE);

        // ========== ASSERT ==========
        assertNotNull(inactiveUsers);

        // Verificar que hay al menos 1 usuario INACTIVE (u004)
        assertFalse(inactiveUsers.isEmpty());

        // Verificar que TODOS son INACTIVE
        inactiveUsers.forEach(user -> {
            var userEntity = userRepository.findById(user.id()).orElseThrow();
            assertEquals(UserStatus.INACTIVE, userEntity.getStatus());
        });
    }
}