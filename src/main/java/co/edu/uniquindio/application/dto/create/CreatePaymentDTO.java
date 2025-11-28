package co.edu.uniquindio.application.dto.create;

import co.edu.uniquindio.application.models.enums.PaymentMethod;
import jakarta.validation.constraints.*;

public record CreatePaymentDTO(
        @NotBlank(message = "El ID de la reserva es requerido")
        String reservationId,

        @NotNull(message = "El monto es requerido")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
        Double amount,

        @NotNull(message = "El método de pago es requerido")
        PaymentMethod paymentMethod,

        String transactionReference
) {
}