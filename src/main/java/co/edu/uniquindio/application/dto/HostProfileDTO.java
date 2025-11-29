package co.edu.uniquindio.application.dto;

import java.time.LocalDateTime;

public record HostProfileDTO(
        String id,
        String userId,
        String userName,
        String userEmail,
        String aboutMe,
        String legalDocument,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}