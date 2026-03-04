package com.pdiosquez.flight_booking_api.infrastructure.rest.error;

import java.time.LocalDateTime;

public record ApiError(
        String code,
        String message,
        LocalDateTime timestamp
) {}
