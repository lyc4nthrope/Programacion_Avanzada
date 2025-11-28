package co.edu.uniquindio.application.dto;

import co.edu.uniquindio.application.models.vo.Answer;
import java.time.LocalDateTime;

public record ReviewDTO(
        Long id,
        String accommodationId,
        String userId,
        String userName,
        String comment,
        Integer rating,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Answer answer
) {
}