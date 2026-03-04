package com.pdiosquez.flight_booking_api.infrastructure.rest.dtos.response;

import com.pdiosquez.flight_booking_api.domain.model.BookingStatus;

public record BookingResponse(
        Long bookingId,
        BookingStatus status,
        PassengerResponse passenger,
        FlightResponse flight
) {}
