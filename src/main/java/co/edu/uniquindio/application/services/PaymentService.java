package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.create.CreatePaymentDTO;
import co.edu.uniquindio.application.dto.PaymentDTO;
import co.edu.uniquindio.application.models.enums.PaymentStatus;

import java.util.List;

public interface PaymentService {

    // Crear un nuevo pago
    void create(CreatePaymentDTO paymentDTO) throws Exception;

    // Obtener un pago por ID
    PaymentDTO get(String id) throws Exception;

    // Obtener pago por reserva
    PaymentDTO getByReservation(String reservationId) throws Exception;

    // Listar todos los pagos
    List<PaymentDTO> listAll();

    // Listar pagos por estado
    List<PaymentDTO> listByStatus(PaymentStatus status);

    // Procesar un pago
    void processPayment(String paymentId) throws Exception;

    // Completar un pago
    void completePayment(String paymentId) throws Exception;

    // Fallar un pago
    void failPayment(String paymentId, String reason) throws Exception;

    // Reembolsar un pago
    void refundPayment(String paymentId) throws Exception;

    // Obtener total de pagos completados
    Double getTotalCompletedPayments();

    // Contar pagos por estado
    Long countByStatus(PaymentStatus status);
}