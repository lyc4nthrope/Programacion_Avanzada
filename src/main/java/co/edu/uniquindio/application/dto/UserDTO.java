package co.edu.uniquindio.application.dto;

import co.edu.uniquindio.application.model.Role;
import java.time.LocalDate;

public record UserDTO(
        String id,
        String name,
        String phone,
        String email,
        String photoUrl,
        LocalDate dateBirth,
        Role role
) {
}