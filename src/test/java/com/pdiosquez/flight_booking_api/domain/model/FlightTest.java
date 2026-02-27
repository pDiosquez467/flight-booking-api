package com.pdiosquez.flight_booking_api.domain.model;

import com.pdiosquez.flight_booking_api.domain.exception.FlightAlreadyDepartedException;
import com.pdiosquez.flight_booking_api.domain.exception.FlightOverbookedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Flight Domain Entity Tests")
class FlightTest {

    @Test
    @DisplayName("Given valid inputs, when create() is called, then a transient flight is instantiated")
    void givenValidInputs_whenCreate_thenTransientFlightInstantiated() {
        String origin               = "BUE";
        String destination          = "MAD";
        int capacity                = 100;
        LocalDateTime departureTime = LocalDateTime.of(2026, 1, 1, 1, 10, 0);

        Flight flight = Flight.create(origin, destination, capacity, departureTime);

        assertAll(
                "Verify newly created transient flight state",
                () -> assertNull(flight.getId(), "A newly created flight must have a null ID"),
                () -> assertEquals(origin, flight.getOrigin()),
                () -> assertEquals(destination, flight.getDestination()),
                () -> assertEquals(capacity, flight.getCapacity()),
                () -> assertEquals(0, flight.getOccupiedSeats()),
                () -> assertEquals(departureTime, flight.getDepartureTime())
        );
    }

    @Test
    @DisplayName("Given a blank origin, when create() is called, then it should throw an IllegalArgumentException")
    void givenBlankOrigin_whenCreate_thenThrowException() {
        String blankOrigin          = " ";
        String destination          = "MAD";
        int capacity                = 100;
        LocalDateTime departureTime = LocalDateTime.of(2026, 1, 1, 1, 10, 0);
        String expectedMessage      = "Origin cannot be blank";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Flight.create(blankOrigin, destination, capacity, departureTime)
        );

        assertTrue(
                exception.getMessage().contains(expectedMessage),
                "The exception message should contain our domain invariant rule"
        );
    }

    @Test
    @DisplayName("Given a blank destination, when create() is called, then it should throw an IllegalArgumentException")
    void givenBlankDestination_whenCreate_thenThrowException() {
        String origin               = "BUE";
        String blankDestination     = " ";
        int capacity                = 100;
        LocalDateTime fixedNow      = LocalDateTime.of(2026, 1, 1, 1, 10, 0);
        String expectedMessage      = "Destination cannot be blank";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Flight.create(origin, blankDestination, capacity, fixedNow)
        );

        assertTrue(
                exception.getMessage().contains(expectedMessage),
                "The exception message should contain our domain invariant rule"
        );
    }

    @Test
    @DisplayName("Given valid persisted data, when fromPersistence() is called, then a restored flight is instantiated")
    void givenPersistedData_whenFromPersistence_thenRestoredFlightInstantiated() {
        Long existingId             = 101L;
        String origin               = "BUE";
        String destination          = "MAD";
        int capacity                = 100;
        int occupiedSeats           = 99;
        LocalDateTime departureTime = LocalDateTime.of(2026, 1, 1, 1, 10, 0);

        Flight restoredFlight = Flight.fromPersistence(
                existingId,
                origin,
                destination,
                capacity,
                occupiedSeats,
                departureTime
        );

        assertAll(
                "Verify restored flight state from persistence",
                () -> assertEquals(existingId, restoredFlight.getId()),
                () -> assertEquals(origin, restoredFlight.getOrigin()),
                () -> assertEquals(destination, restoredFlight.getDestination()),
                () -> assertEquals(capacity, restoredFlight.getCapacity()),
                () -> assertEquals(occupiedSeats, restoredFlight.getOccupiedSeats()),
                () -> assertEquals(departureTime, restoredFlight.getDepartureTime())
        );
    }

    @Test
    @DisplayName("Given a null ID, when fromPersistence() is called, then it should throw an IllegalArgumentException")
    void givenNullId_whenFromPersistence_thenThrowsException() {
        String origin               = "BUE";
        String destination          = "MAD";
        int capacity                = 100;
        int occupiedSeats           = 99;
        LocalDateTime departureTime = LocalDateTime.of(2026, 1, 1, 1, 10, 0);
        String expectedMessage      = "ID is required for persisted flight";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Flight.fromPersistence(
                        null,
                        origin,
                        destination,
                        capacity,
                        occupiedSeats,
                        departureTime
                )
        );

        assertTrue(
                exception.getMessage().contains(expectedMessage),
                "The exception message should contain our domain invariant rule"
        );
    }

    @Test
    @DisplayName("Given valid inputs, when reserveSeat() is called, then a seat is occupied correctly")
    void givenValidInputs_whenReserveSeat_thenSeatIsOccupiedCorrectly() {
        Long id                     = 101L;
        String origin               = "BUE";
        String destination          = "MAD";
        int capacity                = 100;
        int occupiedSeats           = 99;
        LocalDateTime departureTime = LocalDateTime.of(2026, 1, 1, 1, 10, 0);

        Flight flight = Flight.fromPersistence(
                id,
                origin,
                destination,
                capacity,
                occupiedSeats,
                departureTime
        );

        flight.reserveSeat(departureTime.minusDays(5));

        assertAll(
                "Verify successful seat reservation and state consistency",
                () -> assertEquals(100, flight.getOccupiedSeats(), "Occupied seats should increment by 1"),
                () -> assertEquals(0, flight.availableSeats(), "Available seats should be 0 after reserving the last seat"),
                () -> assertEquals(id, flight.getId(), "Flight ID should remain unchanged")
        );
    }

    @Test
    @DisplayName("Given a null current time, when reserveSeat() is called, then it should throw an IllegalArgumentException")
    void givenNullCurrentTime_whenReserveSeat_thenThrowsException() {
        Long id                     = 101L;
        String origin               = "BUE";
        String destination          = "MAD";
        int capacity                = 100;
        int occupiedSeats           = 99;
        LocalDateTime departureTime = LocalDateTime.of(2026, 1, 1, 1, 10, 0);
        String expectedMessage      = "Current time is required";

        Flight flight = Flight.fromPersistence(
                id,
                origin,
                destination,
                capacity,
                occupiedSeats,
                departureTime
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> flight.reserveSeat(null)
        );

        assertAll(
                "Verify exception and side effects",
                () -> assertTrue(exception.getMessage().contains(expectedMessage), "Exception message should contain the invariant rule"),
                () -> assertEquals(99, flight.getOccupiedSeats(), "Occupied seats should not increment if reservation fails")
        );
    }

    @Test
    @DisplayName("Given an already departed flight, when reserveSeat() is called, then it should throw a FlightAlreadyDepartedException")
    void givenFlightAlreadyDeparted_whenReserveSeat_thenThrowsException() {
        Long id                     = 101L;
        String origin               = "BUE";
        String destination          = "MAD";
        int capacity                = 100;
        int occupiedSeats           = 99;
        LocalDateTime departureTime = LocalDateTime.of(2026, 1, 1, 1, 10, 0);
        String expectedMessage      = "Flight %d has already departed. Operations are not allowed.".formatted(id);

        Flight flight = Flight.fromPersistence(
                id,
                origin,
                destination,
                capacity,
                occupiedSeats,
                departureTime
        );

        FlightAlreadyDepartedException exception = assertThrows(
                FlightAlreadyDepartedException.class,
                () -> flight.reserveSeat(departureTime.plusDays(5))
        );

        assertAll(
                "Verify exception and side effects",
                () -> assertTrue(exception.getMessage().contains(expectedMessage), "Exception message should contain the invariant rule"),
                () -> assertEquals(99, flight.getOccupiedSeats(), "Occupied seats should not increment if reservation fails")
        );
    }

    @Test
    @DisplayName("Given an overbooked flight, when reserveSeat() is called, then it should throw a FlightOverbookedException")
    void givenOverbookedFlight_whenReserveSeat_thenThrowsException() {
        Long id                     = 101L;
        String origin               = "BUE";
        String destination          = "MAD";
        int capacity                = 100;
        int occupiedSeats           = 100;
        LocalDateTime departureTime = LocalDateTime.of(2026, 1, 1, 1, 10, 0);
        String expectedMessage      = "Flight %d is fully booked. No seats available.".formatted(id);

        Flight flight = Flight.fromPersistence(
                id,
                origin,
                destination,
                capacity,
                occupiedSeats,
                departureTime
        );

        FlightOverbookedException exception = assertThrows(
                FlightOverbookedException.class,
                () -> flight.reserveSeat(departureTime.minusDays(5))
        );

        assertAll(
                "Verify exception and side effects",
                () -> assertTrue(exception.getMessage().contains(expectedMessage), "Exception message should contain the invariant rule"),
                () -> assertEquals(100, flight.getOccupiedSeats(), "Occupied seats should not increment if reservation fails")
        );
    }
}