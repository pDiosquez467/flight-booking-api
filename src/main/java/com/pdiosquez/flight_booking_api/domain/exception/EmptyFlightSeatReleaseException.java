package com.pdiosquez.flight_booking_api.domain.exception;

public class EmptyFlightSeatReleaseException extends DomainException {
    public EmptyFlightSeatReleaseException(Long flightId) {
        super("Cannot release seat for Flight %d because it has no occupied seats.".formatted(flightId));
    }
}
