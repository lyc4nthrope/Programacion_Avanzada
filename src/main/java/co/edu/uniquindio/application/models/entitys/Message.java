package co.edu.uniquindio.application.models.entitys;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "mensaje", indexes = {
        @Index(name = "idx_chat", columnList = "chat_id"),
        @Index(name = "idx_remitente", columnList = "remitente_id"),
        @Index(name = "idx_leido", columnList = "leido")
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Id
    private String id;  // UUID

    @Column(nullable = false, length = 1000)
    private String contenido;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaEnvio;

    @Column(nullable = false)
    private Boolean leido;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "remitente_id", nullable = false)
    private Usuario remitente;

    @ManyToOne
    @JoinColumn(name = "destinatario_id", nullable = false)
    private Usuario destinatario;

    @PrePersist
    protected void onCreate() {
        if (fechaEnvio == null) {
            fechaEnvio = LocalDateTime.now();
        }
        if (leido == null) {
            leido = false;
        }
    }
}