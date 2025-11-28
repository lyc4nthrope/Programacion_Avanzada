package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.create.CreateUserDTO;
import co.edu.uniquindio.application.dto.edit.EditUserDTO;
import co.edu.uniquindio.application.dto.UserDTO;
import co.edu.uniquindio.application.exceptions.ValueConflictException;
import co.edu.uniquindio.application.models.entitys.User;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.UserService;
import co.edu.uniquindio.application.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    public void create(CreateUserDTO userDTO) throws Exception {
        //Validación del email
        if(isEmailDuplicated(userDTO.email())){
            throw new ValueConflictException("El correo electrónico ya está en uso.");
        }

        //Transformación del DTO a User
        User newUser = userMapper.toEntity(userDTO);
        newUser.setPassword(encode(userDTO.password()));

        //Almacenamiento del usuario
        userRepository.save(newUser);
    }

    private boolean isEmailDuplicated(String email){
        return userRepository.findByEmail(email).isPresent();
    }


    @Override
    public UserDTO get(String id) throws Exception {
        //Recuperación del usuario
        Optional<User> userOptional = userRepository.findById(id);

        //Validación del usuario
        if (userOptional.isEmpty()) {
            throw new Exception("Usuario no encontrado.");
        }

        //Transformación del usuario a DTO
        return userMapper.toUserDTO(userOptional.get());
    }


    @Override
    public void delete(String id) throws Exception {
        //Recuperación del usuario
        Optional<User> userOptional = userRepository.findById(id);

        //Validación del usuario
        if (userOptional.isEmpty()) {
            throw new Exception("Usuario no encontrado.");
        }

        //Eliminación del usuario
        userRepository.delete(userOptional.get());
    }


    @Override
    public List<UserDTO> listAll() {
        return userRepository.findAll()
                .stream().map(userMapper::toUserDTO)
                .toList();
    }


    @Override
    public void edit(String id, EditUserDTO userDTO) throws Exception {
        //Recuperación del usuario
        Optional<User> userOptional = userRepository.findById(id);

        //Validación del usuario
        if (userOptional.isEmpty()) {
            throw new Exception("Usuario no encontrado.");
        }

        //Se obtiene el usuario que está dentro del Optional
        User user = userOptional.get();

        //Actualización de los datos del usuario
        user.setName(userDTO.name());
        user.setPhone(userDTO.phone());
        user.setDateBirth(userDTO.dateBirth());
        user.setPhotoUrl(userDTO.photoUrl());

        //Almacenamiento del usuario
        userRepository.save(user);
    }

    private String encode(String password){
        var passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }


}
