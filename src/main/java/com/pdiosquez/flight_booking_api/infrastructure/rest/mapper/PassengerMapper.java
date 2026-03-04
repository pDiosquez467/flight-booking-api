package com.pdiosquez.flight_booking_api.infrastructure.rest.mapper;

import com.pdiosquez.flight_booking_api.domain.model.Passenger;
import com.pdiosquez.flight_booking_api.infrastructure.rest.dtos.response.PassengerResponse;
import org.springframework.stereotype.Component;

@Component
public class PassengerMapper {

    public PassengerResponse toResponse(Passenger passenger) {
        if (passenger == null) {
            return null;
        }

        return new PassengerResponse(
                passenger.getId(),
                passenger.getName(),
                passenger.getEmail()
        );
    }
}
