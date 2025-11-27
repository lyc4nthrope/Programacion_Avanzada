package co.edu.uniquindio.application.models.entitys;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorite", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "accommodation_id"})
})
@Getter @Setter
@Builder @AllArgsConstructor @NoArgsConstructor
public class Favorite {
    @Id private String id;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
