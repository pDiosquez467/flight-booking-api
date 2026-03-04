package com.pdiosquez.flight_booking_api.infrastructure.rest.dtos.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record FlightRequest(
        @NotBlank(message = "Origin cannot be blank") String origin,
        @NotBlank(message = "Destination cannot be blank") String destination,
        @Positive(message = "Capacity should be positive") int capacity,
        @NotNull(message = "Departure Time is required") @Future(message = "Departure time must be in the future") LocalDateTime departureTime
) {}
