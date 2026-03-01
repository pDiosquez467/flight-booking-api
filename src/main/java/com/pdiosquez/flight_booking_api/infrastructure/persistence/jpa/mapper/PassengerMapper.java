package com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.mapper;

import com.pdiosquez.flight_booking_api.domain.model.Passenger;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.entity.PassengerEntity;
import org.springframework.stereotype.Component;

@Component
public class PassengerMapper {

    public PassengerEntity toEntity(Passenger passenger) {
        if (passenger == null) return null;
        return new PassengerEntity(
                passenger.getId(),
                passenger.getName(),
                passenger.getEmail()
        );
    }

    public Passenger toDomain(PassengerEntity passengerEntity) {
        if (passengerEntity == null) return null;
        return Passenger.fromPersistence(
                passengerEntity.getId(),
                passengerEntity.getName(),
                passengerEntity.getEmail()
        );
    }
}
