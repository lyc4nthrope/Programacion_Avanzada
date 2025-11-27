package co.edu.uniquindio.application.models.entitys;

import co.edu.uniquindio.application.models.vo.Respuesta;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "resena", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"reserva_id"})
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    @Id
    private String id;  // UUID

    @Column(nullable = false)
    private Float calificacion;  // 1-5 estrellas

    @Column(length = 2000)
    private String comentario;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime creadoEn;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime actualizadoEn;

    @Embedded
    private Respuesta respuesta;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "alojamiento_id", nullable = false)
    private Alojamiento alojamiento;

    @OneToOne
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;

    @PrePersist
    protected void onCreate() {
        if (creadoEn == null) {
            creadoEn = LocalDateTime.now();
        }
        if (actualizadoEn == null) {
            actualizadoEn = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
    }
}