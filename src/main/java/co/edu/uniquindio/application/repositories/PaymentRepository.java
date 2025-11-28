package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.models.entitys.Payment;
import co.edu.uniquindio.application.models.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    // Buscar pago por reserva
    Optional<Payment> findByReservationId(String reservationId);

    // Buscar pagos por estado
    List<Payment> findByStatus(PaymentStatus status);

    // Buscar pagos completados
    @Query("SELECT p FROM Payment p WHERE p.status = 'COMPLETED'")
    List<Payment> findCompletedPayments();

    // Buscar pagos fallidos
    @Query("SELECT p FROM Payment p WHERE p.status = 'FAILED'")
    List<Payment> findFailedPayments();

    // Contar pagos por estado
    Long countByStatus(PaymentStatus status);

    // Buscar por referencia de transacción
    Optional<Payment> findByTransactionReference(String transactionReference);
}