package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.create.CreatePaymentDTO;
import co.edu.uniquindio.application.dto.PaymentDTO;
import co.edu.uniquindio.application.exceptions.InvalidOperationException;
import co.edu.uniquindio.application.exceptions.NotFoundException;
import co.edu.uniquindio.application.mappers.PaymentMapper;
import co.edu.uniquindio.application.models.entitys.Payment;
import co.edu.uniquindio.application.models.entitys.Reservation;
import co.edu.uniquindio.application.models.enums.PaymentStatus;
import co.edu.uniquindio.application.repositories.PaymentRepository;
import co.edu.uniquindio.application.repositories.ReservationRepository;
import co.edu.uniquindio.application.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ReservationRepository reservationRepository;

    @Override
    public void create(CreatePaymentDTO paymentDTO) throws Exception {
        // Validar que la reserva existe
        Optional<Reservation> reservation = reservationRepository.findById(paymentDTO.reservationId());
        if (reservation.isEmpty()) {
            throw new NotFoundException("La reserva con ID '" + paymentDTO.reservationId() + "' no fue encontrada.");
        }

        // Validar que el monto coincida con el precio total de la reserva
        if (!paymentDTO.amount().equals(reservation.get().getTotalPrice())) {
            throw new InvalidOperationException("El monto (" + paymentDTO.amount() +
                    ") no coincide con el precio total de la reserva (" + reservation.get().getTotalPrice() + ").");
        }

        // Validar que no exista un pago previo para esta reserva
        Optional<Payment> existingPayment = paymentRepository.findByReservationId(paymentDTO.reservationId());
        if (existingPayment.isPresent()) {
            throw new InvalidOperationException("Ya existe un pago registrado para esta reserva.");
        }

        // Crear el pago
        Payment newPayment = paymentMapper.toEntity(paymentDTO);
        newPayment.setReservation(reservation.get());

        // Guardar el pago
        paymentRepository.save(newPayment);
    }

    @Override
    public PaymentDTO get(String id) throws Exception {
        Optional<Payment> paymentOptional = paymentRepository.findById(id);

        if (paymentOptional.isEmpty()) {
            throw new NotFoundException("El pago con ID '" + id + "' no fue encontrado.");
        }

        return paymentMapper.toPaymentDTO(paymentOptional.get());
    }

    @Override
    public PaymentDTO getByReservation(String reservationId) throws Exception {
        Optional<Payment> paymentOptional = paymentRepository.findByReservationId(reservationId);

        if (paymentOptional.isEmpty()) {
            throw new NotFoundException("No se encontró un pago para la reserva con ID '" + reservationId + "'.");
        }

        return paymentMapper.toPaymentDTO(paymentOptional.get());
    }

    @Override
    public List<PaymentDTO> listAll() {
        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toPaymentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDTO> listByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status)
                .stream()
                .map(paymentMapper::toPaymentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void processPayment(String paymentId) throws Exception {
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);

        if (paymentOptional.isEmpty()) {
            throw new NotFoundException("El pago con ID '" + paymentId + "' no fue encontrado.");
        }

        Payment payment = paymentOptional.get();

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidOperationException("Solo se pueden procesar pagos en estado PENDING.");
        }

        // Simular procesamiento de pago (en producción, integrar con proveedor de pagos)
        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);
    }

    @Override
    public void completePayment(String paymentId) throws Exception {
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);

        if (paymentOptional.isEmpty()) {
            throw new NotFoundException("El pago con ID '" + paymentId + "' no fue encontrado.");
        }

        Payment payment = paymentOptional.get();

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new InvalidOperationException("El pago ya está completado.");
        }

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new InvalidOperationException("No se puede completar un pago reembolsado.");
        }

        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);
    }

    @Override
    public void failPayment(String paymentId, String reason) throws Exception {
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);

        if (paymentOptional.isEmpty()) {
            throw new NotFoundException("El pago con ID '" + paymentId + "' no fue encontrado.");
        }

        Payment payment = paymentOptional.get();

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidOperationException("Solo se pueden fallar pagos en estado PENDING.");
        }

        payment.setStatus(PaymentStatus.FAILED);
        if (reason != null && !reason.isEmpty()) {
            payment.setTransactionReference(reason);
        }
        paymentRepository.save(payment);
    }

    @Override
    public void refundPayment(String paymentId) throws Exception {
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);

        if (paymentOptional.isEmpty()) {
            throw new NotFoundException("El pago con ID '" + paymentId + "' no fue encontrado.");
        }

        Payment payment = paymentOptional.get();

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new InvalidOperationException("Solo se pueden reembolsar pagos completados.");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);
    }

    @Override
    public Double getTotalCompletedPayments() {
        return paymentRepository.findCompletedPayments()
                .stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    @Override
    public Long countByStatus(PaymentStatus status) {
        return paymentRepository.countByStatus(status);
    }
}