package com.pdiosquez.flight_booking_api.infrastructure.rest.error;

import com.pdiosquez.flight_booking_api.domain.exception.BookingNotFoundException;
import com.pdiosquez.flight_booking_api.domain.exception.FlightNotFoundException;
import com.pdiosquez.flight_booking_api.domain.exception.PassengerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PassengerNotFoundException.class)
    public ResponseEntity<ApiError> handlePassengerNotFound(PassengerNotFoundException exception) {
        return buildError(
                "PASSENGER_NOT_FOUND",
                exception.getMessage(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FlightNotFoundException.class)
    public ResponseEntity<ApiError> handleFlightNotFound(FlightNotFoundException exception) {
        return buildError(
                "FLIGHT_NOT_FOUND",
                exception.getMessage(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ApiError> handleBookingNotFound(BookingNotFoundException exception) {
        return buildError(
                "BOOKING_NOT_FOUND",
                exception.getMessage(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        return buildError(
                "VALIDATION_ERROR",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage();

        return buildError(
                "BAD_REQUEST_VALIDATION_ERROR",
                errorMessage,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception exception) {
        return buildError(
                "INTERNAL_SERVER_ERROR",
                "Unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiError> buildError(String code,
                                                String message,
                                                HttpStatus status) {
        ApiError error = new ApiError(
                code,
                message,
                LocalDateTime.now());

        return ResponseEntity
                .status(status)
                .body(error);
    }
}
