package com.pdiosquez.flight_booking_api.domain.exception;

public class PassengerNotFoundException extends ResourceNotFoundException {
    public PassengerNotFoundException(Long id) {
        super("Passenger with ID %d not found.".formatted(id));
    }
}
