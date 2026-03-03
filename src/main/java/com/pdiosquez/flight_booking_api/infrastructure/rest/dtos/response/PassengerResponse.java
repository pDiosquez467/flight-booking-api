package com.pdiosquez.flight_booking_api.infrastructure.rest.dtos.response;

public record PassengerResponse(
        Long id,
        String name,
        String email) {
}
