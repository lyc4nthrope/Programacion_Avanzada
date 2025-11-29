package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.create.CreateHostProfileDTO;
import co.edu.uniquindio.application.dto.edit.EditHostProfileDTO;
import co.edu.uniquindio.application.dto.HostProfileDTO;

import java.util.List;

public interface HostProfileService {

    // Crear un nuevo perfil de anfitrión
    void create(CreateHostProfileDTO hostProfileDTO) throws Exception;

    // Obtener un perfil de anfitrión por ID
    HostProfileDTO get(String id) throws Exception;

    // Obtener perfil de anfitrión por usuario
    HostProfileDTO getByUser(String userId) throws Exception;

    // Eliminar un perfil de anfitrión
    void delete(String id) throws Exception;

    // Listar todos los perfiles de anfitrión
    List<HostProfileDTO> listAll();

    // Editar un perfil de anfitrión
    void edit(String id, EditHostProfileDTO hostProfileDTO) throws Exception;

    // Verificar si un usuario tiene perfil de anfitrión
    boolean hasHostProfile(String userId);

    // Obtener información del anfitrión
    HostProfileDTO getHostInfo(String userId) throws Exception;
}