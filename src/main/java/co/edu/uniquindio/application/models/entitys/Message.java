package co.edu.uniquindio.application.models.entitys;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message", indexes = {
        @Index(name = "idx_chat", columnList = "chat_id"),
        @Index(name = "idx_sender", columnList = "sender_id"),
        @Index(name = "idx_read", columnList = "is_read")
})
@Getter @Setter
@Builder @AllArgsConstructor @NoArgsConstructor
public class Message {
    @Id private String id;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime sentAt;

    @Column(nullable = false)
    private Boolean isRead;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @PrePersist
    protected void onCreate() {
        if (sentAt == null) sentAt = LocalDateTime.now();
        if (isRead == null) isRead = false;
    }
}