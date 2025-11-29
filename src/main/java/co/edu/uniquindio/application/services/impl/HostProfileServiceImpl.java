package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.create.CreateHostProfileDTO;
import co.edu.uniquindio.application.dto.edit.EditHostProfileDTO;
import co.edu.uniquindio.application.dto.HostProfileDTO;
import co.edu.uniquindio.application.exceptions.InvalidOperationException;
import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.mappers.HostProfileMapper;
import co.edu.uniquindio.application.models.entitys.HostProfile;
import co.edu.uniquindio.application.models.entitys.User;
import co.edu.uniquindio.application.repositories.HostProfileRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.HostProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HostProfileServiceImpl implements HostProfileService {

    private final HostProfileRepository hostProfileRepository;
    private final HostProfileMapper hostProfileMapper;
    private final UserRepository userRepository;

    @Override
    public void create(CreateHostProfileDTO hostProfileDTO) throws Exception {
        // Validar que el usuario existe
        Optional<User> user = userRepository.findById(hostProfileDTO.userId());
        if (user.isEmpty()) {
            throw new NotFoundException("El usuario con ID '" + hostProfileDTO.userId() + "' no fue encontrado.");
        }

        // Validar que el usuario no tenga ya un perfil de anfitrión
        if (hostProfileRepository.existsByUserId(hostProfileDTO.userId())) {
            throw new InvalidOperationException("El usuario ya tiene un perfil de anfitrión registrado.");
        }

        // Crear el perfil de anfitrión
        HostProfile newHostProfile = hostProfileMapper.toEntity(hostProfileDTO);
        newHostProfile.setUser(user.get());

        // Guardar el perfil
        hostProfileRepository.save(newHostProfile);
    }

    @Override
    public HostProfileDTO get(String id) throws Exception {
        Optional<HostProfile> hostProfileOptional = hostProfileRepository.findById(id);

        if (hostProfileOptional.isEmpty()) {
            throw new NotFoundException("El perfil de anfitrión con ID '" + id + "' no fue encontrado.");
        }

        return hostProfileMapper.toHostProfileDTO(hostProfileOptional.get());
    }

    @Override
    public HostProfileDTO getByUser(String userId) throws Exception {
        Optional<HostProfile> hostProfileOptional = hostProfileRepository.findByUserId(userId);

        if (hostProfileOptional.isEmpty()) {
            throw new NotFoundException("El usuario con ID '" + userId + "' no tiene un perfil de anfitrión.");
        }

        return hostProfileMapper.toHostProfileDTO(hostProfileOptional.get());
    }

    @Override
    public void delete(String id) throws Exception {
        Optional<HostProfile> hostProfileOptional = hostProfileRepository.findById(id);

        if (hostProfileOptional.isEmpty()) {
            throw new NotFoundException("El perfil de anfitrión con ID '" + id + "' no fue encontrado.");
        }

        hostProfileRepository.deleteById(id);
    }

    @Override
    public List<HostProfileDTO> listAll() {
        return hostProfileRepository.findAll()
                .stream()
                .map(hostProfileMapper::toHostProfileDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void edit(String id, EditHostProfileDTO hostProfileDTO) throws Exception {
        Optional<HostProfile> hostProfileOptional = hostProfileRepository.findById(id);

        if (hostProfileOptional.isEmpty()) {
            throw new NotFoundException("El perfil de anfitrión con ID '" + id + "' no fue encontrado.");
        }

        HostProfile hostProfile = hostProfileOptional.get();

        // Actualizar campos
        hostProfile.setAboutMe(hostProfileDTO.aboutMe());
        hostProfile.setLegalDocument(hostProfileDTO.legalDocument());

        // Guardar cambios
        hostProfileRepository.save(hostProfile);
    }

    @Override
    public boolean hasHostProfile(String userId) {
        if (userId == null || userId.isEmpty()) {
            return false;
        }
        return hostProfileRepository.existsByUserId(userId);
    }

    @Override
    public HostProfileDTO getHostInfo(String userId) throws Exception {
        // Validar que el usuario existe
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("El usuario con ID '" + userId + "' no fue encontrado.");
        }

        return getByUser(userId);
    }
}