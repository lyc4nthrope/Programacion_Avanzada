package co.edu.uniquindio.application.models.entitys;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "foto_alojamiento")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccommodationPhoto {

    @Id
    private String id;  // UUID

    @Column(nullable = false)
    private String urlImagen;

    @Column(nullable = false)
    private Boolean esPrincipal;

    @Column(nullable = false)
    private Integer orden;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime creadoEn;

    // Relación
    @ManyToOne
    @JoinColumn(name = "alojamiento_id", nullable = false)
    private Alojamiento alojamiento;

    @PrePersist
    protected void onCreate() {
        if (creadoEn == null) {
            creadoEn = LocalDateTime.now();
        }
        if (esPrincipal == null) {
            esPrincipal = false;
        }
    }
}