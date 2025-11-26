package co.edu.uniquindio.application.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Accommodation {
    private String id;
    private String title;
    private String description;
    private String city;
    private String address;
    private Double latitude;
    private Double longitude;
    private BigDecimal pricePerNight;
    private Integer maxCapacity;
    private String amenities;
    private String hostId;
    private Double averageRating;
    private LocalDateTime createdAt;
    private AccommodationStatus status;
}