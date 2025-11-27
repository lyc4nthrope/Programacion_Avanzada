package co.edu.uniquindio.application.models.entitys;

import co.edu.uniquindio.application.models.enums.Estado;
import co.edu.uniquindio.application.models.enums.Servicio;
import co.edu.uniquindio.application.models.vo.Direccion;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "alojamiento", indexes = {
        @Index(name = "idx_ciudad", columnList = "direccion_ciudad"),
        @Index(name = "idx_anfitrion", columnList = "anfitrion_id"),
        @Index(name = "idx_estado", columnList = "estado"),
        @Index(name = "idx_precio", columnList = "precio_por_noche")
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Accommodation {

    @Id
    private String id;  // UUID

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Embedded
    @Column(nullable = false)
    private Direccion address;

    @Column(nullable = false)
    private Integer maxGuests;

    @Column(nullable = false)
    private Float pricePerNight;

    @ElementCollection(targetClass = Servicio.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "alojamiento_servicios", joinColumns = @JoinColumn(name = "alojamiento_id"))
    @Enumerated(EnumType.STRING)
    private Set<Servicio> services = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado status;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column
    private Double averageRating;

    @Column
    private Integer ratingCount;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "anfitrion_id", nullable = false)
    private Usuario host;

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL)
    private List<AccommodationPhoto> photos;

    @OneToMany(mappedBy = "alojamiento")
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "alojamiento")
    private List<Review> reviews;

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL)
    private List<AvailabilityCalendar> calendar;

    @OneToMany(mappedBy = "alojamiento")
    private List<Favorite> favorites;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = Estado.ACTIVE;
        }
        if (averageRating == null) {
            averageRating = 0.0;
        }
        if (ratingCount == null) {
            ratingCount = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}