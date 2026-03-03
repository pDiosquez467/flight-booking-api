package com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.mapper;

import com.pdiosquez.flight_booking_api.domain.model.Flight;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.entity.FlightEntity;
import org.springframework.stereotype.Component;

@Component
public class FlightMapper {

    public FlightEntity toEntity(Flight flight) {
        if (flight == null) return null;

        return new FlightEntity(
                flight.getId(),
                flight.getOrigin(),
                flight.getDestination(),
                flight.getCapacity(),
                flight.getOccupiedSeats(),
                flight.getDepartureTime()
        );
    }

    public Flight toDomain(FlightEntity flightEntity) {
        if (flightEntity == null) return null;

        return Flight.fromPersistence(
                flightEntity.getId(),
                flightEntity.getOrigin(),
                flightEntity.getDestination(),
                flightEntity.getCapacity(),
                flightEntity.getOccupiedSeats(),
                flightEntity.getDepartureTime()
        );
    }
}
