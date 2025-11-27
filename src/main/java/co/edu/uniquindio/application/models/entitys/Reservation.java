package co.edu.uniquindio.application.models.entitys;

import co.edu.uniquindio.application.models.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation", indexes = {
        @Index(name = "idx_guest", columnList = "guest_id"),
        @Index(name = "idx_accommodation", columnList = "accommodation_id"),
        @Index(name = "idx_dates", columnList = "check_in_date, check_out_date"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter @Setter
@Builder @AllArgsConstructor @NoArgsConstructor
public class Reservation {
    @Id private String id;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Column(nullable = false)
    private Integer numberOfGuests;

    @Column(nullable = false)
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @ManyToOne
    @JoinColumn(name = "guest_id", nullable = false)
    private User guest;

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private Payment payment;

    @OneToOne(mappedBy = "reservation")
    private Review review;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
        if (status == null) status = ReservationStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}