package co.edu.uniquindio.application.models.entitys;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_code", indexes = {
        @Index(name = "idx_user", columnList = "user_id"),
        @Index(name = "idx_code", columnList = "code"),
        @Index(name = "idx_expires", columnList = "expires_at")
})
@Getter @Setter
@Builder @AllArgsConstructor @NoArgsConstructor
public class PasswordResetCode {
    @Id private String id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean used;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (expiresAt == null) expiresAt = LocalDateTime.now().plusMinutes(15);
        if (used == null) used = false;
    }
}