package co.edu.uniquindio.application.dto;

import co.edu.uniquindio.application.models.enums.PaymentMethod;
import co.edu.uniquindio.application.models.enums.PaymentStatus;
import java.time.LocalDateTime;

public record PaymentDTO(
        String id,
        String reservationId,
        Double amount,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        String transactionReference,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}