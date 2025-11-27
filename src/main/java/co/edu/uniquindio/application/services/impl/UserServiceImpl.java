package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.CreateUserDTO;
import co.edu.uniquindio.application.dto.EditUserDTO;
import co.edu.uniquindio.application.dto.UserDTO;
import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.exceptions.ValueConflictException;
import co.edu.uniquindio.application.mappers.UserMapper;
import co.edu.uniquindio.application.models.entitys.User;
import co.edu.uniquindio.application.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final Map<String, User> userStore = new ConcurrentHashMap<>();

    @Override
    public void create(CreateUserDTO userDTO) throws Exception {
        // Validación para verificar si el email ya está en uso
        if(isEmailDuplicated(userDTO.email())){
            throw new ValueConflictException("El correo electrónico ya está en uso.");
        }

        // Transformación del DTO a User
        User newUser = userMapper.toEntity(userDTO);
        newUser.setPassword(encode(userDTO.password()));

        // Almacenamiento del usuario
        userStore.put(newUser.getId(), newUser);
    }

    @Override
    public UserDTO get(String id) throws Exception {
        // Recuperación del usuario
        User user = userStore.get(id);

        // Si el usuario no existe, lanzar una excepción
        if (user == null) {
            throw new NotFoundException("El usuario con ID '" + id + "' no fue encontrado.");
        }

        // Transformación del usuario a DTO
        return userMapper.toUserDTO(user);
    }

    @Override
    public void delete(String id) throws Exception {
        // Recuperación del usuario
        User user = userStore.get(id);

        // Si el usuario no existe, lanzar una excepción
        if (user == null) {
            throw new NotFoundException("El usuario con ID '" + id + "' no fue encontrado.");
        }

        // Eliminación del usuario
        userStore.remove(id);
    }

    @Override
    public List<UserDTO> listAll() {
        // Obtener todos los usuarios y convertirlos a DTOs
        return userStore.values()
                .stream()
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void edit(String id, EditUserDTO userDTO) throws Exception {
        // Recuperación del usuario
        User user = userStore.get(id);

        // Si el usuario no existe, lanzar una excepción
        if (user == null) {
            throw new NotFoundException("El usuario con ID '" + id + "' no fue encontrado.");
        }

        // Actualización de los campos del usuario
        user.setName(userDTO.name());
        user.setPhone(userDTO.phone());
        user.setPhotoUrl(userDTO.photoUrl());
        user.setDateBirth(userDTO.dateBirth());
        user.setRole(userDTO.role());

        // Actualización en el almacenamiento
        userStore.put(id, user);
    }

    // ===== MÉTODOS AUXILIARES =====

    /**
     * Verifica si un email ya está registrado
     */
    private boolean isEmailDuplicated(String email) {
        return userStore.values()
                .stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    /**
     * Codifica/encripta la contraseña usando BCrypt
     */
    private String encode(String password) {
        var passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }
}