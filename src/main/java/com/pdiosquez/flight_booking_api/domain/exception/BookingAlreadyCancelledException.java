package com.pdiosquez.flight_booking_api.domain.exception;

public class BookingAlreadyCancelledException extends DomainException {
    public BookingAlreadyCancelledException(Long bookingId) {
        super("Booking %d is already cancelled and cannot be cancelled again.".formatted(bookingId));
    }
}
