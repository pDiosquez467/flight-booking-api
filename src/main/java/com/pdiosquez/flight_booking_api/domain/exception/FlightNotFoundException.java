package com.pdiosquez.flight_booking_api.domain.exception;

public class FlightNotFoundException extends ResourceNotFoundException {
    public FlightNotFoundException(Long id) {
        super("Flight with ID %d not found.".formatted(id));
    }
}
