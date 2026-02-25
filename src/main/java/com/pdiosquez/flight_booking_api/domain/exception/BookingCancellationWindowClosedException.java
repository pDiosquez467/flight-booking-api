package com.pdiosquez.flight_booking_api.domain.exception;

public class BookingCancellationWindowClosedException extends DomainException{
    public BookingCancellationWindowClosedException(Long bookingId) {
        super("Cannot cancel Booking %d because the flight has already departed.".formatted(bookingId));
    }
}
