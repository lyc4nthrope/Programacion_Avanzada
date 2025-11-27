package co.edu.uniquindio.application.models.entitys;

import co.edu.uniquindio.application.models.enums.MetodoPago;
import co.edu.uniquindio.application.models.enums.EstadoPago;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "pago", indexes = {
        @Index(name = "idx_reserva", columnList = "reserva_id"),
        @Index(name = "idx_estado", columnList = "estado")
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pay {

    @Id
    private String id;  // UUID

    @Column(nullable = false)
    private Double monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estado;

    @Column
    private String referenciaTransaccion;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime creadoEn;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime actualizadoEn;

    // Relación
    @OneToOne
    @JoinColumn(name = "reserva_id", nullable = false, unique = true)
    private Reserva reserva;

    @PrePersist
    protected void onCreate() {
        if (creadoEn == null) {
            creadoEn = LocalDateTime.now();
        }
        if (actualizadoEn == null) {
            actualizadoEn = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoPago.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
    }
}