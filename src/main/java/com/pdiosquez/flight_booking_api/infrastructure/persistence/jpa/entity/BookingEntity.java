package com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.entity;

import com.pdiosquez.flight_booking_api.domain.model.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passenger_id", nullable = false)
    private PassengerEntity passengerEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "flight_id", nullable = false)
    private FlightEntity flightEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
