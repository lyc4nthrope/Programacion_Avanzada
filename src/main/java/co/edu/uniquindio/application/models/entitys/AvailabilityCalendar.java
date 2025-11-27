package co.edu.uniquindio.application.models.entitys;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "availability_calendar", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"accommodation_id", "date"})
})
@Getter @Setter
@Builder @AllArgsConstructor @NoArgsConstructor
public class AvailabilityCalendar {
    @Id private String id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Boolean available;

    @Column private String reason;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (available == null) available = true;
    }
}
