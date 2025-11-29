package co.edu.uniquindio.application.models.entitys;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accommodation", indexes = {
        @Index(name = "idx_city", columnList = "city"),
        @Index(name = "idx_price", columnList = "price_per_night"),
        @Index(name = "idx_rating", columnList = "average_rating")
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
    // mappedBy="place" indica que Review es el propietario
    // cascade = ALL significa que si eliminas Place, también se eliminan sus Reviews
    // fetch = LAZY significa que las reviews se cargan bajo demanda (más eficiente)
    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private User host;  // Referencia al usuario anfitrión

    // Ciclo de vida
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
        if (averageRating == null) averageRating = 0.0;
        if (ratingCount == null) ratingCount = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}