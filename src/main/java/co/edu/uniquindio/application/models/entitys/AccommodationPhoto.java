package co.edu.uniquindio.application.models.entitys;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "accommodation_photo")
@Getter @Setter
@Builder @AllArgsConstructor @NoArgsConstructor
public class AccommodationPhoto {
    @Id private String id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private Boolean isPrimary;

    @Column(nullable = false)
    private Integer displayOrder;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (isPrimary == null) isPrimary = false;
    }
}
