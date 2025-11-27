package co.edu.uniquindio.application.models.entitys;

import co.edu.uniquindio.application.models.enums.Rol;
import co.edu.uniquindio.application.models.enums.Estado;
import co.edu.uniquindio.application.models.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "usuario", indexes = {
        @Index(name = "idx_email", columnList = "email", unique = true),
        @Index(name = "idx_rol", columnList = "rol"),
        @Index(name = "idx_estado", columnList = "estado")
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private String id;  // UUID

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 150)
    private String password;  // Encriptada con BCrypt

    @Column(nullable = false, length = 15)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(length = 300)
    private String photo;  // URL en Cloudinary

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado status;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    // Relaciones
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private PerfilAnfitrion hostProfile;

    @OneToMany(mappedBy = "host")
    private List<Accommodation> accommodations;

    @OneToMany(mappedBy = "guest")
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "usuario")
    private List<Review> reviews;

    @OneToMany(mappedBy = "usuario")
    private List<PasswordResetCode> resetCodes;

    @ManyToMany(mappedBy = "usuarios")
    private List<Chat> chats;

    @OneToMany(mappedBy = "sender")
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "recipient")
    private List<Message> receivedMessages;

    @OneToMany(mappedBy = "usuario")
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
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}