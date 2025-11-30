package co.edu.uniquindio.application.models.entitys;

import co.edu.uniquindio.application.models.vo.Answer;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Entity
@Table(name = "review", indexes = {
        @Index(name = "idx_accommodation", columnList = "accommodation_id"),
        @Index(name = "idx_user", columnList = "user_id"),
        @Index(name = "idx_reservation", columnList = "reservation_id")
})
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ⭐ RELACIÓN BIDIRECCIONAL CON ACCOMMODATION - Review es propietario
    @ManyToOne
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    // ⭐ RELACIÓN CON USER
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ✅ NUEVO: RELACIÓN BIDIRECCIONAL CON RESERVATION
    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Lob
    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Embedded
    private Answer answer;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}