package co.edu.uniquindio.application.models.entitys;

import co.edu.uniquindio.application.models.enums.Role;
import co.edu.uniquindio.application.models.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user", indexes = {
        @Index(name = "idx_email", columnList = "email", unique = true),
        @Index(name = "idx_role", columnList = "role"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter @Setter
@Builder @AllArgsConstructor @NoArgsConstructor
public class User {
    // Identificador único
    @Id private String id;

    // Información básica
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 150)
    private String password; // Encriptada

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(length = 300)
    private String photo; // URL en Cloudinary

    @Column(nullable = false)
    private LocalDate dateBirth;

    // Roles y estado
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    // Auditoría
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    // Relaciones
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private HostProfile hostProfile;

    @OneToMany(mappedBy = "host")
    private List<Accommodation> accommodations;

    @OneToMany(mappedBy = "guest")
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "user")
    private List<Review> reviews;

    @OneToMany(mappedBy = "user")
    private List<PasswordResetCode> resetCodes;

    @ManyToMany(mappedBy = "users")
    private List<Chat> chats;

    @OneToMany(mappedBy = "sender")
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "recipient")
    private List<Message> receivedMessages;

    @OneToMany(mappedBy = "user")
    private List<Favorite> favorites;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
        if (status == null) status = status.ACTIVE;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}