package com.pdiosquez.flight_booking_api.domain.exception;

public class BookingNotFoundException extends ResourceNotFoundException {
    public BookingNotFoundException(Long id) {
        super("Booking with ID %d not found.".formatted(id));
    }
}