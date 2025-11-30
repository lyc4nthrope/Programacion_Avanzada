package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.LoginDTO;
import co.edu.uniquindio.application.dto.TokenDTO;
import co.edu.uniquindio.application.dto.create.CreateUserDTO;
import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.mappers.UserMapper;
import co.edu.uniquindio.application.models.entitys.User;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.security.JWTUtils;
import co.edu.uniquindio.application.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;

    @Override
    public void register(CreateUserDTO userDTO) throws Exception {
        // Validar si el email ya existe
        if (userRepository.findByEmail(userDTO.email()).isPresent()) {
            throw new Exception("El email ya está registrado");
        }

        // Crear el usuario
        User newUser = userMapper.toEntity(userDTO);
        
        // Encriptar la contraseña
        newUser.setPassword(passwordEncoder.encode(userDTO.password()));
        
        // Guardar el usuario
        userRepository.save(newUser);
    }

    @Override
    public TokenDTO login(LoginDTO loginDTO) throws Exception {
        // Buscar el usuario por email
        Optional<User> optionalUser = userRepository.findByEmail(loginDTO.email());

        if (optionalUser.isEmpty()) {
            throw new NotFoundException("El usuario no existe");
        }

        User user = optionalUser.get();

        // Verificar si la contraseña es correcta
        if (!passwordEncoder.matches(loginDTO.password(), user.getPassword())) {
            throw new Exception("Contraseña incorrecta");
        }

        // Generar el token JWT
        String token = jwtUtils.generateToken(user.getId(), createClaims(user));
        
        return new TokenDTO(token);
    }

    @Override
    public String getAuthenticatedUserId() {
        org.springframework.security.core.userdetails.User user = 
            (org.springframework.security.core.userdetails.User) 
            SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        return user.getUsername(); // El username es el ID del usuario
    }

    /**
     * Crea los claims (datos adicionales) para incluir en el token JWT
     */
    private Map<String, String> createClaims(User user) {
        return Map.of(
            "email", user.getEmail(),
            "name", user.getName(),
            "role", "ROLE_" + user.getRole().name()
        );
    }
}