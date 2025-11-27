package co.edu.uniquindio.application.models.entitys;

import co.edu.uniquindio.application.models.enums.Status;
import co.edu.uniquindio.application.models.enums.Service;
import co.edu.uniquindio.application.models.enums.UserStatus;
import co.edu.uniquindio.application.models.vo.Address;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "accommodation", indexes = {
        @Index(name = "idx_city", columnList = "address_city"),
        @Index(name = "idx_host", columnList = "host_id"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter @Setter
@Builder @AllArgsConstructor @NoArgsConstructor
public class Accommodation {
    // Identificación
    @Id private String id;

    // Información básica
    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Embedded
    @Column(nullable = false)
    private Address address;

    @Column(nullable = false)
    private Integer maxCapacity;

    @Column(nullable = false)
    private Double pricePerNight;

    // Servicios
    @ElementCollection(targetClass = Service.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "accommodation_services", joinColumns = @JoinColumn(name = "accommodation_id"))
    @Enumerated(EnumType.STRING)
    private Set<Service> services = new HashSet<>();

    // Estado
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    // Auditoría
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    // Métricas
    @Column private Double averageRating;
    @Column private Integer ratingCount;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL)
    private List<AccommodationPhoto> photos;

    @OneToMany(mappedBy = "accommodation")
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "accommodation")
    private List<Review> reviews;

    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL)
    private List<AvailabilityCalendar> calendar;

    @OneToMany(mappedBy = "accommodation")
    private List<Favorite> favorites;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
        if (status == null) status = status.ACTIVE;
        if (averageRating == null) averageRating = 0.0;
        if (ratingCount == null) ratingCount = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}