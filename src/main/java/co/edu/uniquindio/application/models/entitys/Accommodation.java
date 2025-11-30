package co.edu.uniquindio.application.models.entitys;

import co.edu.uniquindio.application.models.enums.AccommodationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accommodation", indexes = {
        @Index(name = "idx_city", columnList = "city"),
        @Index(name = "idx_price", columnList = "price_per_night"),
        @Index(name = "idx_rating", columnList = "average_rating"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter @Setter
@Builder @AllArgsConstructor @NoArgsConstructor
public class Accommodation {

    @Id
    private String id;  // UUID

    // Información básica
    @Column(length = 150, nullable = false)
    private String title;

    @Column(length = 2000, nullable = false)
    private String description;

    @Column(length = 100, nullable = false)
    private String city;

    @Column(length = 150, nullable = false)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double pricePerNight;

    @Column(nullable = false)
    private Integer maxCapacity;

    @Column(length = 500)
    private String amenities;

    // ✅ NUEVO: Estado del alojamiento
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccommodationStatus status;

    // Auditoría
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    // Métricas
    @Column
    private Double averageRating;

    @Column
    private Integer ratingCount;

    // ⭐ RELACIÓN BIDIRECCIONAL CON REVIEW
    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    // ⭐ RELACIÓN CON HOST (Usuario anfitrión)
    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    // Ciclo de vida
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
        if (averageRating == null) averageRating = 0.0;
        if (ratingCount == null) ratingCount = 0;
        if (status == null) status = AccommodationStatus.ACTIVE; // ✅ Por defecto ACTIVE
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}