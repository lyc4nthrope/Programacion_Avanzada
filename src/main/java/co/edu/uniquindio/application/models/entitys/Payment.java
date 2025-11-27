package co.edu.uniquindio.application.models.entitys;

import co.edu.uniquindio.application.models.enums.PaymentMethod;
import co.edu.uniquindio.application.models.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment", indexes = {
        @Index(name = "idx_reservation", columnList = "reservation_id"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter @Setter
@Builder @AllArgsConstructor @NoArgsConstructor
public class Payment {
    @Id private String id;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column private String transactionReference;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
        if (status == null) status = PaymentStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
