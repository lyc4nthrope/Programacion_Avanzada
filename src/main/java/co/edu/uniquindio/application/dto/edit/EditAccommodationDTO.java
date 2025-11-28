package co.edu.uniquindio.application.dto.edit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;
import java.math.BigDecimal;

public record EditAccommodationDTO(
        @NotBlank @Length(max = 100) String title,
        @NotBlank @Length(max = 500) String description,
        @NotBlank @Length(max = 100) String city,
        @NotBlank @Length(max = 150) String address,
        @NotNull Double latitude,
        @NotNull Double longitude,
        @NotNull @Positive BigDecimal pricePerNight,
        @NotNull @Positive Integer maxCapacity,
        String amenities
) {
}