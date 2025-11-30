package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.create.CreateUserDTO;
import co.edu.uniquindio.application.dto.edit.EditUserDTO;
import co.edu.uniquindio.application.dto.UserDTO;
import co.edu.uniquindio.application.exceptions.InvalidOperationException;
import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.exceptions.ValueConflictException;
import co.edu.uniquindio.application.models.entitys.User;
import co.edu.uniquindio.application.models.enums.UserStatus;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.UserService;
import co.edu.uniquindio.application.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void create(CreateUserDTO userDTO) throws Exception {
        // Validación del email
        if (isEmailDuplicated(userDTO.email())) {
            throw new ValueConflictException("El correo electrónico ya está en uso.");
        }

        // Transformación del DTO a User
        User newUser = userMapper.toEntity(userDTO);
        newUser.setPassword(passwordEncoder.encode(userDTO.password()));

        // Almacenamiento del usuario
        userRepository.save(newUser);
    }

    private boolean isEmailDuplicated(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public UserDTO get(String id) throws Exception {
        // Recuperación del usuario
        Optional<User> userOptional = userRepository.findById(id);

        // Validación del usuario
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Usuario con ID '" + id + "' no encontrado.");
        }

        // Transformación del usuario a DTO
        return userMapper.toUserDTO(userOptional.get());
    }

    @Override
    public void delete(String id) throws Exception {
        // Recuperación del usuario
        Optional<User> userOptional = userRepository.findById(id);

        // Validación del usuario
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Usuario con ID '" + id + "' no encontrado.");
        }

        // Eliminación física del usuario
        userRepository.delete(userOptional.get());
    }

    @Override
    public List<UserDTO> listAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void edit(String id, EditUserDTO userDTO) throws Exception {
        // Recuperación del usuario
        Optional<User> userOptional = userRepository.findById(id);

        // Validación del usuario
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Usuario con ID '" + id + "' no encontrado.");
        }

        // Se obtiene el usuario que está dentro del Optional
        User user = userOptional.get();

        // Actualización de los datos del usuario
        user.setName(userDTO.name());
        user.setPhone(userDTO.phone());
        user.setDateBirth(userDTO.dateBirth());
        user.setPhotoUrl(userDTO.photoUrl());
        user.setRole(userDTO.role());

        // Almacenamiento del usuario
        userRepository.save(user);
    }

    // ✅ NUEVOS MÉTODOS PARA GESTIONAR STATUS

    @Override
    public void activateUser(String id) throws Exception {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            throw new NotFoundException("Usuario con ID '" + id + "' no encontrado.");
        }

        User user = userOptional.get();

        // Validar que no esté eliminado
        if (user.getStatus() == UserStatus.DELETED) {
            throw new InvalidOperationException(
                "No se puede activar un usuario eliminado. Debe crearse uno nuevo."
            );
        }

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    @Override
    public void deactivateUser(String id) throws Exception {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            throw new NotFoundException("Usuario con ID '" + id + "' no encontrado.");
        }

        User user = userOptional.get();
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    @Override
    public void softDeleteUser(String id) throws Exception {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            throw new NotFoundException("Usuario con ID '" + id + "' no encontrado.");
        }

        User user = userOptional.get();
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    @Override
    public List<UserDTO> listByStatus(UserStatus status) {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getStatus() == status)
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> listActiveUsers() {
        return listByStatus(UserStatus.ACTIVE);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public UserDTO getByEmail(String email) throws Exception {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new NotFoundException("No existe un usuario con el email: " + email);
        }

        return userMapper.toUserDTO(userOptional.get());
    }
}