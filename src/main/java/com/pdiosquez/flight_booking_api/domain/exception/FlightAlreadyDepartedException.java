package com.pdiosquez.flight_booking_api.domain.exception;

public class FlightAlreadyDepartedException extends DomainException {
    public FlightAlreadyDepartedException(Long flightId) {
        super("Flight %d has already departed. Operations are not allowed.".formatted(flightId));
    }
}
