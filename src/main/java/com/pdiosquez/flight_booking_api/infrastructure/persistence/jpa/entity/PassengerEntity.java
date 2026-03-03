package com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "passengers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PassengerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String name;

    @Column(name = "email_address", nullable = false, unique = true)
    private String email;

    public static PassengerEntity of(String name, String email) {
        PassengerEntity entity = new PassengerEntity();
        entity.name = name;
        entity.email = email;
        return entity;
    }
}