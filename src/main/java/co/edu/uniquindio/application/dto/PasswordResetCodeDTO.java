package co.edu.uniquindio.application.dto;

import java.time.LocalDateTime;

public record PasswordResetCodeDTO(
        String id,
        String userId,
        String code,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        Boolean used
) {
}