package com.pdiosquez.flight_booking_api.domain.exception;

public class FlightOverbookedException extends DomainException {
    public FlightOverbookedException(Long flightId) {
        super("Flight %d is fully booked. No seats available.".formatted(flightId));
    }
}
