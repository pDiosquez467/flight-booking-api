package com.pdiosquez.flight_booking_api.domain.model;

import com.pdiosquez.flight_booking_api.domain.exception.BookingAlreadyCancelledException;
import com.pdiosquez.flight_booking_api.domain.exception.BookingCancellationWindowClosedException;
import com.pdiosquez.flight_booking_api.domain.util.DomainValidation;

import java.time.LocalDateTime;
import java.util.Objects;

public class Booking {
    private final Long id;
    private final Passenger passenger;
    private final Flight flight;
    private BookingStatus status;
    private final LocalDateTime createdAt;

    private Booking(Long id, Passenger passenger, Flight flight, BookingStatus status, LocalDateTime createdAt) {
        DomainValidation.notNull(passenger, "Passenger is required for a booking.");
        DomainValidation.notNull(flight, "Flight is required for a booking.");
        DomainValidation.notNull(status, "Booking status cannot be null.");
        DomainValidation.notNull(createdAt, "Created date cannot be null.");

        this.id = id;
        this.passenger = passenger;
        this.flight = flight;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Booking create(Passenger passenger, Flight flight, LocalDateTime currentTime) {
        return new Booking(null, passenger, flight, BookingStatus.CONFIRMED, currentTime);
    }

    public static Booking fromPersistence(Long id, Passenger passenger, Flight flight, BookingStatus status, LocalDateTime createdAt) {
        DomainValidation.notNull(id, "Booking ID is required for persistence reconstruction.");
        return new Booking(id, passenger, flight, status, createdAt);
    }

    /**
     * Cancels the booking if business rules allow it.
     * Rules:
     * 1. The booking must not be already canceled.
     * 2. The flight must not have departed yet.
     * Side effects:
     * - Changes booking status to CANCELLED.
     * - Releases the seat in the associated Flight.
     *
     * @param currentTime The time at which the cancellation is requested.
     * @throws BookingAlreadyCancelledException If booking is already canceled.
     * @throws BookingCancellationWindowClosedException If flight has departed.
     */
    public void cancel(LocalDateTime currentTime) {
        DomainValidation.notNull(currentTime, "Current time is required for cancellation.");

        validateBookingIsActive();
        validateFlightHasNotDeparted(currentTime);
        flight.releaseSeat(currentTime);
        this.status = BookingStatus.CANCELLED;
    }

    private void validateBookingIsActive() {
        if (this.status == BookingStatus.CANCELLED) {
            throw new BookingAlreadyCancelledException(this.id);
        }
    }

    private void validateFlightHasNotDeparted(LocalDateTime currentTime) {
        if (currentTime.isAfter(flight.getDepartureTime())) {
            throw new BookingCancellationWindowClosedException(this.id);
        }
    }

    public Long getId() { return id; }
    public Passenger getPassenger() { return passenger; }
    public Flight getFlight() { return flight; }
    public BookingStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking booking)) return false;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", passenger=" + passenger +
                ", flight=" + flight +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}