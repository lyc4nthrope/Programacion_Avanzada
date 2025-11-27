package co.edu.uniquindio.application.models.vo;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Address {
    private String city;       // Ciudad
    private String street;     // Calle
    private Double latitude;   // Latitud
    private Double longitude;  // Longitud

    @Override
    public String toString() {
        return street + ", " + city;
    }
}