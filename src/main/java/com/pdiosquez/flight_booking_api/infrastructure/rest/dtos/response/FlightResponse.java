package com.pdiosquez.flight_booking_api.infrastructure.rest.dtos.response;

import java.time.LocalDateTime;

public record FlightResponse(
        Long id,
        String origin,
        String destination,
        int capacity,
        int occupiedSeats,
        LocalDateTime departureTime
) {}
