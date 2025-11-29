package co.edu.uniquindio.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ChatDTO(
        String id,
        List<String> participantIds,
        List<String> participantNames,
        Boolean active,
        Long messageCount,
        LocalDateTime createdAt,
        LocalDateTime lastMessageAt
) {
}