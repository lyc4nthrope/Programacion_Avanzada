package co.edu.uniquindio.application.services.unit;

import co.edu.uniquindio.application.dto.create.CreateUserDTO;
import co.edu.uniquindio.application.dto.UserDTO;
import co.edu.uniquindio.application.exceptions.InvalidOperationException;
import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.exceptions.ValueConflictException;
import co.edu.uniquindio.application.mappers.UserMapper;
import co.edu.uniquindio.application.models.entitys.User;
import co.edu.uniquindio.application.models.enums.Role;
import co.edu.uniquindio.application.models.enums.UserStatus;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.AuthService;
import co.edu.uniquindio.application.services.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 *  PRUEBAS UNITARIAS de UserService
 * 
 * ¿Qué son las pruebas unitarias?
 *  Prueban UNA SOLA UNIDAD de código (un metodo)
 *  NO usan base de datos real
 *  Simulan (mock) todas las dependencias
 *  Son MUY RÁPIDAS (milisegundos)
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    // @Mock: Crea una SIMULACIÓN de la dependencia
    @Mock private UserMapper userMapper;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthService authService;

    // @InjectMocks: Inyecta los mocks en el servicio
    @InjectMocks
    private UserServiceImpl userService;

    // ========================================
    // PRUEBAS DEL METODO create()
    // ========================================

    @Test
    @DisplayName("Crear usuario exitosamente cuando el email NO existe")
    void testCreateUser_Success() throws Exception {
        // ========== ARRANGE (Preparar) ==========
        // 1. Crear el DTO de entrada
        CreateUserDTO userDTO = new CreateUserDTO(
                "Juan Pérez",
                "3001234567",
                "juan@email.com",
                "Password123",
                "http://photo.url",
                LocalDate.of(1990, 1, 1),
                Role.GUEST
        );

        // 2. Simular que el email NO existe
        when(userRepository.findByEmail(userDTO.email()))
                .thenReturn(Optional.empty());

        // 3. Simular el mapeo del DTO a entidad
        User userEntity = new User();
        userEntity.setId("user-123");
        userEntity.setEmail(userDTO.email());
        userEntity.setName(userDTO.name());
        userEntity.setRole(userDTO.role());
        userEntity.setStatus(UserStatus.ACTIVE);
        userEntity.setCreatedAt(LocalDateTime.now());

        when(userMapper.toEntity(userDTO)).thenReturn(userEntity);

        // 4. Simular el encoder de contraseña
        when(passwordEncoder.encode("Password123"))
                .thenReturn("$2a$10$encoded_password");

        // 5. Simular el guardado (devuelve el mismo usuario)
        when(userRepository.save(any(User.class))).thenReturn(userEntity);

        // ========== ACT (Actuar) ==========
        // Ejecutar el metodo que queremos probar
        // assertDoesNotThrow: Verifica que NO se lance ninguna excepción
        assertDoesNotThrow(() -> userService.create(userDTO));

        // ========== ASSERT (Afirmar) ==========
        // Verificar que se llamó a los métodos esperados
        verify(userRepository, times(1)).findByEmail(userDTO.email());
        verify(userMapper, times(1)).toEntity(userDTO);
        verify(passwordEncoder, times(1)).encode("Password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Crear usuario lanza excepción cuando el email YA EXISTE")
    void testCreateUser_EmailAlreadyExists() {
        // ========== ARRANGE ==========
        CreateUserDTO userDTO = new CreateUserDTO(
                "María López",
                "3009876543",
                "maria@email.com",
                "Password123",
                "http://photo.url",
                LocalDate.of(1995, 5, 15),
                Role.GUEST
        );

        // Simular que el email YA EXISTE
        User existingUser = new User();
        existingUser.setId("existing-user-id");
        existingUser.setEmail("maria@email.com");
        existingUser.setName("María López");

        when(userRepository.findByEmail(userDTO.email()))
                .thenReturn(Optional.of(existingUser));

        // ========== ACT & ASSERT ==========
        // Verificar que se lanza la excepción correcta
        ValueConflictException exception = assertThrows(
                ValueConflictException.class,
                () -> userService.create(userDTO)
        );

        // Verificar el mensaje de la excepción
        assertEquals("El correo electrónico ya está en uso.", exception.getMessage());

        // Verificar que NO se intentó guardar nada
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Crear usuario con rol ADMIN lanza excepción")
    void testCreateUser_AdminRoleNotAllowed() {
        // ========== ARRANGE ==========
        CreateUserDTO userDTO = new CreateUserDTO(
                "Admin User",
                "3001112233",
                "admin@email.com",
                "Password123",
                "",
                LocalDate.of(1990, 1, 1),
                Role.ADMIN  // ❌ Intentar crear ADMIN
        );

        // Simular que el email NO existe
        when(userRepository.findByEmail(userDTO.email()))
                .thenReturn(Optional.empty());

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
    @DisplayName("Obtener usuario exitosamente cuando existe")
    void testGetUser_Success() throws Exception {
        // ========== ARRANGE ==========
        String userId = "user-123";

        User userEntity = new User();
        userEntity.setId(userId);
        userEntity.setName("Juan Pérez");
        userEntity.setEmail("juan@email.com");
        userEntity.setRole(Role.GUEST);
        userEntity.setStatus(UserStatus.ACTIVE);

        UserDTO expectedDTO = new UserDTO(
                userId,
                "Juan Pérez",
                "3001234567",
                "juan@email.com",
                "",
                LocalDate.of(1990, 1, 1),
                Role.GUEST
        );

        // Simular que el usuario existe
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(userEntity));

        // Simular el mapeo a DTO
        when(userMapper.toUserDTO(userEntity)).thenReturn(expectedDTO);

        // ========== ACT ==========
        UserDTO result = userService.get(userId);

        // ========== ASSERT ==========
        assertNotNull(result);
        assertEquals(expectedDTO.id(), result.id());
        assertEquals(expectedDTO.name(), result.name());
        assertEquals(expectedDTO.email(), result.email());
        assertEquals(expectedDTO.role(), result.role());

        // Verificar que se llamaron los métodos
        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toUserDTO(userEntity);
    }

    @Test
    @DisplayName("Obtener usuario lanza excepción cuando NO existe")
    void testGetUser_NotFound() {
        // ========== ARRANGE ==========
        String userId = "non-existent-id";

        // Simular que el usuario NO existe
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // ========== ACT & ASSERT ==========
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.get(userId)
        );

        assertTrue(exception.getMessage().contains(userId));
        verify(userMapper, never()).toUserDTO(any());
    }

    // ========================================
    // PRUEBAS DEL METODO delete()
    // ========================================

    @Test
    @DisplayName("Eliminar usuario exitosamente cuando es el propietario")
    void testDeleteUser_Success() throws Exception {
        // ========== ARRANGE ==========
        String userId = "user-123";

        User userEntity = new User();
        userEntity.setId(userId);
        userEntity.setName("Juan Pérez");

        // Simular que el usuario autenticado es el mismo que se quiere eliminar
        when(authService.getAuthenticatedUserId()).thenReturn(userId);

        // Simular que el usuario existe
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(userEntity));

        // ========== ACT ==========
        assertDoesNotThrow(() -> userService.delete(userId));

        // ========== ASSERT ==========
        verify(userRepository, times(1)).delete(userEntity);
    }

    @Test
    @DisplayName("Eliminar usuario lanza excepción cuando NO es el propietario")
    void testDeleteUser_NotOwner() {
        // ========== ARRANGE ==========
        String userId = "user-123";
        String authenticatedUserId = "other-user-456";

        // Simular que el usuario autenticado es DIFERENTE
        when(authService.getAuthenticatedUserId())
                .thenReturn(authenticatedUserId);

        User userEntity = new User();
        userEntity.setId(userId);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(userEntity));

        // ========== ACT & ASSERT ==========
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> userService.delete(userId)
        );

        assertTrue(exception.getMessage().contains("tu propia cuenta"));
        verify(userRepository, never()).delete(any());
    }

    // ========================================
    // PRUEBAS DE GESTIÓN DE ESTADO
    // ========================================

    @Test
    @DisplayName("Activar usuario exitosamente")
    void testActivateUser_Success() throws Exception {
        // ========== ARRANGE ==========
        String userId = "user-123";

        User userEntity = new User();
        userEntity.setId(userId);
        userEntity.setStatus(UserStatus.INACTIVE);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(User.class)))
                .thenReturn(userEntity);

        // ========== ACT ==========
        assertDoesNotThrow(() -> userService.activateUser(userId));

        // ========== ASSERT ==========
        assertEquals(UserStatus.ACTIVE, userEntity.getStatus());
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("Activar usuario DELETED lanza excepción")
    void testActivateUser_DeletedUser() {
        // ========== ARRANGE ==========
        String userId = "user-123";

        User userEntity = new User();
        userEntity.setId(userId);
        userEntity.setStatus(UserStatus.DELETED);  // ❌ Usuario eliminado

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(userEntity));

        // ========== ACT & ASSERT ==========
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> userService.activateUser(userId)
        );

        assertTrue(exception.getMessage().contains("eliminado"));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Desactivar usuario exitosamente")
    void testDeactivateUser_Success() throws Exception {
        // ========== ARRANGE ==========
        String userId = "user-123";

        User userEntity = new User();
        userEntity.setId(userId);
        userEntity.setStatus(UserStatus.ACTIVE);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(User.class)))
                .thenReturn(userEntity);

        // ========== ACT ==========
        assertDoesNotThrow(() -> userService.deactivateUser(userId));

        // ========== ASSERT ==========
        assertEquals(UserStatus.INACTIVE, userEntity.getStatus());
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("Soft delete usuario exitosamente")
    void testSoftDeleteUser_Success() throws Exception {
        // ========== ARRANGE ==========
        String userId = "user-123";

        User userEntity = new User();
        userEntity.setId(userId);
        userEntity.setStatus(UserStatus.ACTIVE);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(User.class)))
                .thenReturn(userEntity);

        // ========== ACT ==========
        assertDoesNotThrow(() -> userService.softDeleteUser(userId));

        // ========== ASSERT ==========
        assertEquals(UserStatus.DELETED, userEntity.getStatus());
        verify(userRepository, times(1)).save(userEntity);
    }

    // ========================================
    // PRUEBA DEL METODO authenticate()
    // ========================================

    @Test
    @DisplayName("Autenticar usuario con credenciales correctas")
    void testAuthenticate_Success() throws Exception {
        // ========== ARRANGE ==========
        String email = "juan@email.com";
        String password = "Password123";

        User userEntity = new User();
        userEntity.setId("user-123");
        userEntity.setEmail(email);
        userEntity.setPassword("$2a$10$encoded_password");
        userEntity.setStatus(UserStatus.ACTIVE);
        userEntity.setName("Juan Pérez");
        userEntity.setRole(Role.GUEST);

        UserDTO expectedDTO = new UserDTO(
                "user-123",
                "Juan Pérez",
                "3001234567",
                email,
                "",
                LocalDate.of(1990, 1, 1),
                Role.GUEST
        );

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(password, userEntity.getPassword()))
                .thenReturn(true);
        when(userMapper.toUserDTO(userEntity))
                .thenReturn(expectedDTO);

        // ========== ACT ==========
        UserDTO result = userService.authenticate(email, password);

        // ========== ASSERT ==========
        assertNotNull(result);
        assertEquals(expectedDTO.email(), result.email());
        assertEquals(expectedDTO.name(), result.name());
    }

    @Test
    @DisplayName("Autenticar con contraseña incorrecta lanza excepción")
    void testAuthenticate_WrongPassword() {
        // ========== ARRANGE ==========
        String email = "juan@email.com";
        String wrongPassword = "WrongPassword";

        User userEntity = new User();
        userEntity.setEmail(email);
        userEntity.setPassword("$2a$10$encoded_password");

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(wrongPassword, userEntity.getPassword()))
                .thenReturn(false);

        // ========== ACT & ASSERT ==========
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> userService.authenticate(email, wrongPassword)
        );

        assertTrue(exception.getMessage().contains("Credenciales inválidas"));
    }

    @Test
    @DisplayName("Autenticar usuario INACTIVE lanza excepción")
    void testAuthenticate_InactiveUser() {
        // ========== ARRANGE ==========
        String email = "juan@email.com";
        String password = "Password123";

        User userEntity = new User();
        userEntity.setEmail(email);
        userEntity.setPassword("$2a$10$encoded_password");
        userEntity.setStatus(UserStatus.INACTIVE);  // ❌ Usuario inactivo

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(password, userEntity.getPassword()))
                .thenReturn(true);

        // ========== ACT & ASSERT ==========
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> userService.authenticate(email, password)
        );

        assertTrue(exception.getMessage().contains("no está activo"));
    }
}