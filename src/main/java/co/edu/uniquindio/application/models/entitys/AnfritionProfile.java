package co.edu.uniquindio.application.models.entitys;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "perfil_anfitrion")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnfritionProfile{

    @Id
    private String id;  // UUID

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String sobreMi;

    @Column
    private String documentoLegal;

    // Relación
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;
}