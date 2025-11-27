package co.edu.uniquindio.application.models.vo;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String ciudad;           // Ciudad
    private String calle;            // Calle/Dirección
    private Double latitud;          // Coordenada para mapa
    private Double longitud;         // Coordenada para mapa

    @Override
    public String toString() {
        return calle + ", " + ciudad;
    }
}