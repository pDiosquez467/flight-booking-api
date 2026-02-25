package com.pdiosquez.flight_booking_api.domain.exception;

public abstract class DomainException extends RuntimeException{
    protected DomainException(String message) {
        super(message);
    }
}
