package com.pdiosquez.flight_booking_api.domain.model;

import com.pdiosquez.flight_booking_api.domain.exception.EmptyFlightSeatReleaseException;
import com.pdiosquez.flight_booking_api.domain.exception.FlightAlreadyDepartedException;
import com.pdiosquez.flight_booking_api.domain.exception.FlightOverbookedException;
import com.pdiosquez.flight_booking_api.domain.util.DomainValidation;

import java.time.LocalDateTime;
import java.util.Objects;

public class Flight {
    private final Long id;
    private final String origin;
    private final String destination;
    private final int capacity;
    private int occupiedSeats;
    private final LocalDateTime departureTime;

    private Flight(Long id, String origin, String destination, int capacity, int occupiedSeats, LocalDateTime departureTime) {
        DomainValidation.notBlank(origin, "Origin cannot be blank");
        DomainValidation.notBlank(destination, "Destination cannot be blank");
        DomainValidation.isPositive(capacity, "Capacity must be positive");
        DomainValidation.notNull(departureTime, "Departure time cannot be null");
        DomainValidation.isGreaterOrEqualThan(capacity, occupiedSeats, "Occupied seats cannot exceed capacity");
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.capacity = capacity;
        this.occupiedSeats = occupiedSeats;
        this.departureTime = departureTime;
    }

    public static Flight create(String origin, String destination, int capacity, LocalDateTime departureTime) {
        return new Flight(null, origin, destination, capacity, 0, departureTime);
    }

    public static Flight fromPersistence(Long id, String origin, String destination, int capacity, int occupiedSeats, LocalDateTime departureTime) {
        DomainValidation.notNull(id, "ID is required for persisted flight");
        return new Flight(id, origin, destination, capacity, occupiedSeats, departureTime);
    }

    public void reserveSeat(LocalDateTime currentTime) {
        DomainValidation.notNull(currentTime, "Current time is required");
        validateDepartureTime(currentTime);
        validateCapacity();

        occupiedSeats++;
    }

    public void releaseSeat(LocalDateTime currentTime) {
        DomainValidation.notNull(currentTime, "Current time is required");
        validateDepartureTime(currentTime);
        validateOccupiedSeats();

        occupiedSeats--;
    }

    public int availableSeats() {
        return capacity - occupiedSeats;
    }

    private void validateDepartureTime(LocalDateTime currentTime) {
        if (currentTime.isAfter(departureTime)) {
            throw new FlightAlreadyDepartedException(this.id);
        }
    }

    private void validateCapacity() {
        if (occupiedSeats >= capacity) {
            throw new FlightOverbookedException(this.id);
        }
    }

    private void validateOccupiedSeats() {
        if (occupiedSeats <= 0) {
            throw new EmptyFlightSeatReleaseException(this.id);
        }
    }

    public Long getId() { return id; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public int getCapacity() { return capacity; }
    public int getOccupiedSeats() { return occupiedSeats; }
    public LocalDateTime getDepartureTime() { return departureTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Flight flight)) return false;
        return Objects.equals(id, flight.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}