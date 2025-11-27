package co.edu.uniquindio.application.models.entitys;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "host_profile")
@Getter @Setter
@Builder @AllArgsConstructor @NoArgsConstructor
public class HostProfile {

    @Id
    private String id;  // UUID

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String aboutMe;  // Descripción personal del anfitrión

    @Column
    private String legalDocument;  // Documentos legales (URL en Cloudinary)

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    // Relación: Un perfil pertenece a UN usuario
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

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