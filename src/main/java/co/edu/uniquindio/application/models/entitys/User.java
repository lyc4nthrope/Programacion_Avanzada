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
    @Id
    private String id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 15)
    private String phone;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 200, nullable = false)
    private String password;

    @Column(length = 200)
    private String photoUrl;

    @Column(nullable = false)
    private LocalDate dateBirth;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private UserStatus status;
}