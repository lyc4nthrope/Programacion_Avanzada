package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.create.CreatePaymentDTO;
import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.dto.PaymentDTO;
import co.edu.uniquindio.application.models.enums.PaymentStatus;
import co.edu.uniquindio.application.services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody CreatePaymentDTO paymentDTO) throws Exception {
        paymentService.create(paymentDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(false, "El pago ha sido creado exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<PaymentDTO>> get(@PathVariable String id) throws Exception {
        PaymentDTO paymentDTO = paymentService.get(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, paymentDTO));
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<ResponseDTO<PaymentDTO>> getByReservation(@PathVariable String reservationId) throws Exception {
        PaymentDTO paymentDTO = paymentService.getByReservation(reservationId);
        return ResponseEntity.ok(new ResponseDTO<>(false, paymentDTO));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<PaymentDTO>>> listAll() {
        List<PaymentDTO> list = paymentService.listAll();
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ResponseDTO<List<PaymentDTO>>> listByStatus(@PathVariable PaymentStatus status) {
        List<PaymentDTO> list = paymentService.listByStatus(status);
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PutMapping("/{id}/process")
    public ResponseEntity<ResponseDTO<String>> processPayment(@PathVariable String id) throws Exception {
        paymentService.processPayment(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El pago ha sido procesado exitosamente"));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ResponseDTO<String>> completePayment(@PathVariable String id) throws Exception {
        paymentService.completePayment(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El pago ha sido completado"));
    }

    @PutMapping("/{id}/fail")
    public ResponseEntity<ResponseDTO<String>> failPayment(
            @PathVariable String id,
            @RequestParam(required = false) String reason) throws Exception {
        paymentService.failPayment(id, reason);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El pago ha sido marcado como fallido"));
    }

    @PutMapping("/{id}/refund")
    public ResponseEntity<ResponseDTO<String>> refundPayment(@PathVariable String id) throws Exception {
        paymentService.refundPayment(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El pago ha sido reembolsado"));
    }

    @GetMapping("/stats/total-completed")
    public ResponseEntity<ResponseDTO<Double>> getTotalCompletedPayments() {
        Double total = paymentService.getTotalCompletedPayments();
        return ResponseEntity.ok(new ResponseDTO<>(false, total));
    }

    @GetMapping("/stats/count-by-status")
    public ResponseEntity<ResponseDTO<Map<String, Long>>> countByStatus() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("PENDING", paymentService.countByStatus(PaymentStatus.PENDING));
        stats.put("COMPLETED", paymentService.countByStatus(PaymentStatus.COMPLETED));
        stats.put("FAILED", paymentService.countByStatus(PaymentStatus.FAILED));
        stats.put("REFUNDED", paymentService.countByStatus(PaymentStatus.REFUNDED));
        return ResponseEntity.ok(new ResponseDTO<>(false, stats));
    }
}