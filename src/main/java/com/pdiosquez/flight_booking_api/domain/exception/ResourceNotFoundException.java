package com.pdiosquez.flight_booking_api.domain.exception;

public class ResourceNotFoundException extends DomainException{
    protected ResourceNotFoundException(String message) {
        super(message);
    }
}
