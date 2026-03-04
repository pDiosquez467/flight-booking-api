package com.pdiosquez.flight_booking_api.infrastructure.rest.dtos.request;

import jakarta.validation.constraints.NotNull;

public record BookingRequest(
        @NotNull(message = "Passenger ID is required") Long passengerId,
        @NotNull(message = "Flight ID is required") Long flightId
) {}
