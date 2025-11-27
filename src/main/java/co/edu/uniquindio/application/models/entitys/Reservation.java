package co.edu.uniquindio.application.models.entitys;

import co.edu.uniquindio.application.models.enums.ReservaEstado;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "reserva", indexes = {
        @Index(name = "idx_huesped", columnList = "huesped_id"),
        @Index(name = "idx_alojamiento", columnList = "alojamiento_id"),
        @Index(name = "idx_fechas", columnList = "fecha_entrada, fecha_salida"),
        @Index(name = "idx_estado", columnList = "estado")
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

    @Id
    private String id;  // UUID

    @Column(nullable = false)
    private LocalDate fechaEntrada;

    @Column(nullable = false)
    private LocalDate fechaSalida;

    @Column(nullable = false)
    private Integer cantidadHuespedes;

    @Column(nullable = false)
    private Double precioTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservaEstado estado;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime creadoEn;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime actualizadoEn;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "alojamiento_id", nullable = false)
    private Alojamiento alojamiento;

    @ManyToOne
    @JoinColumn(name = "huesped_id", nullable = false)
    private Usuario huesped;

    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL)
    private Pago pago;

    @OneToOne(mappedBy = "reserva")
    private Resena resena;

    @PrePersist
    protected void onCreate() {
        if (creadoEn == null) {
            creadoEn = LocalDateTime.now();
        }
        if (actualizadoEn == null) {
            actualizadoEn = LocalDateTime.now();
        }
        if (estado == null) {
            estado = ReservaEstado.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
    }
}