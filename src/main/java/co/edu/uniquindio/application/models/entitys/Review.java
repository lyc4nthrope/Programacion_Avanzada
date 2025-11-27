package co.edu.uniquindio.application.models.entitys;

import co.edu.uniquindio.application.models.entitys.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Genera el id automáticamente en la base de datos
    private Long id;

    @ManyToOne //Llave foránea a la entidad User
    @JoinColumn(nullable = false)
    private User user;

    @Lob // Sirve para textos largos
    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false)
    private LocalDateTime createdAt;

}
