package co.edu.uniquindio.application.models.entitys;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "availability_calendar", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"alojamiento_id", "fecha"})
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityCalendar {

    @Id
    private String id;  // UUID

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private Boolean disponible;

    @Column
    private String razon;

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
        if (disponible == null) {
            disponible = true;
        }
    }
}