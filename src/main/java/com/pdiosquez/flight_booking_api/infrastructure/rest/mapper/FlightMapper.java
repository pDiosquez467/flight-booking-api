package com.pdiosquez.flight_booking_api.infrastructure.rest.mapper;

import com.pdiosquez.flight_booking_api.domain.model.Flight;
import com.pdiosquez.flight_booking_api.infrastructure.rest.dtos.response.FlightResponse;
import org.springframework.stereotype.Component;

@Component
public class FlightMapper {

    public FlightResponse toResponse(Flight flight) {
        if (flight == null) {
            return null;
        }

        return new FlightResponse(
                flight.getId(),
                flight.getOrigin(),
                flight.getDestination(),
                flight.getCapacity(),
                flight.getOccupiedSeats(),
                flight.getDepartureTime()
        );
    }
}
