package co.edu.uniquindio.application.models.entitys;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "contrasena_codigo_reinicio", indexes = {
        @Index(name = "idx_usuario", columnList = "usuario_id"),
        @Index(name = "idx_codigo", columnList = "codigo"),
        @Index(name = "idx_expira", columnList = "expira_en")
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetCode {

    @Id
    private String id;  // UUID

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime creadoEn;

    @Column(nullable = false)
    private LocalDateTime expiraEn;

    @Column(nullable = false)
    private Boolean utilizado;

    // Relación
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @PrePersist
    protected void onCreate() {
        if (creadoEn == null) {
            creadoEn = LocalDateTime.now();
        }
        if (expiraEn == null) {
            expiraEn = LocalDateTime.now().plusMinutes(15); // Válido 15 minutos
        }
        if (utilizado == null) {
            utilizado = false;
        }
    }
}
