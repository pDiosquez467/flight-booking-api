package com.pdiosquez.flight_booking_api.domain.model;

import com.pdiosquez.flight_booking_api.domain.util.DomainValidation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Booking Domain Entity Tests")
class BookingTest {

    // --- Helper Methods (Object Mother Pattern) ---

    private Passenger aPassenger() {
        return Passenger.fromPersistence(
                467L,
                "John Doe",
                "john.doe@example.com"
        );
    }

    private Flight aFlight(LocalDateTime departureTime) {
        return Flight.fromPersistence(
                101L,
                "BUE",
                "MAD",
                100,
                99,
                departureTime
        );
    }

    private Flight aFlightThatHasNotDeparted(int capacity, int occupiedSeats, LocalDateTime departureTime) {
        DomainValidation.isGreaterOrEqualThan(capacity, occupiedSeats, "Occupied seats cannot exceed capacity.");
        return Flight.fromPersistence(
                101L,
                "BUE",
                "MAD",
                capacity,
                occupiedSeats,
                departureTime.plusDays(5)
        );
    }

    private LocalDateTime defaultCurrentTime() {
        return LocalDateTime.of(2026, 1, 1, 1, 0);
    }

    // --- Tests ---

    @Test
    @DisplayName("Given valid inputs, when create() is called, then a transient booking is instantiated")
    void givenValidInputs_whenCreate_thenTransientBookingInstantiated() {
        LocalDateTime fixedNow       = defaultCurrentTime();
        BookingStatus expectedStatus = BookingStatus.CONFIRMED;
        Passenger passenger          = aPassenger();
        Flight flight                = aFlight(fixedNow.plusDays(5));

        Booking booking = Booking.create(
                passenger,
                flight,
                fixedNow
        );

        assertAll(
                "Verify newly created transient booking state",
                () -> assertNull(booking.getId(), "A newly created booking must have a null ID"),
                () -> assertEquals(passenger, booking.getPassenger(), "Passenger should match"),
                () -> assertEquals(flight, booking.getFlight(), "Flight should match"),
                () -> assertEquals(expectedStatus, booking.getStatus(), "Initial status must be CONFIRMED"),
                () -> assertEquals(fixedNow, booking.getCreatedAt(), "Creation date should match")
        );
    }

    @Test
    @DisplayName("Given a null passenger, when create() is called, then it should throw an IllegalArgumentException")
    void givenNullPassenger_whenCreate_thenThrowsException() {
        LocalDateTime fixedNow       = defaultCurrentTime();
        Flight flight                = aFlight(fixedNow.plusDays(5));
        String expectedMessagePart   = "Passenger is required for a booking.";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Booking.create(null, flight, fixedNow)
        );

        assertTrue(
                exception.getMessage().contains(expectedMessagePart),
                "Exception message should contain the domain invariant rule"
        );
    }

    @Test
    @DisplayName("Given a null flight, when create() is called, then it should throw an IllegalArgumentException")
    void givenNullFlight_whenCreate_thenThrowsException() {
        LocalDateTime fixedNow       = defaultCurrentTime();
        Passenger passenger          = aPassenger();
        String expectedMessagePart   = "Flight is required for a booking.";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Booking.create(passenger, null, fixedNow)
        );

        assertTrue(
                exception.getMessage().contains(expectedMessagePart),
                "Exception message should contain the domain invariant rule"
        );
    }

    @Test
    @DisplayName("Given a null creation date, when create() is called, then it should throw an IllegalArgumentException")
    void givenNullCreatedAt_whenCreate_thenThrowsException() {
        LocalDateTime fixedNow       = defaultCurrentTime();
        Passenger passenger          = aPassenger();
        Flight flight                = aFlight(fixedNow.plusDays(5));
        String expectedMessagePart   = "Created date cannot be null.";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Booking.create(passenger, flight, null)
        );

        assertTrue(
                exception.getMessage().contains(expectedMessagePart),
                "Exception message should contain the domain invariant rule"
        );
    }

    @Test
    @DisplayName("Given valid inputs, when cancel() is called, then the booking is correctly cancelled")
    void givenValidInputs_whenCancel_thenBookingIsCorrectlyCancelled() {
        int capacity                 = 100;
        int occupiedSeats            = 99;
        LocalDateTime fixedNow       = defaultCurrentTime();
        Passenger passenger          = aPassenger();
        Flight flight                = aFlightThatHasNotDeparted(capacity, occupiedSeats, fixedNow);
        BookingStatus expectedStatus = BookingStatus.CANCELLED;

        Booking booking = Booking.create(
                passenger,
                flight,
                fixedNow
        );

        booking.cancel(fixedNow);

        assertAll(
                "Verify booking cancellation and side effects on the flight",
                () -> assertEquals(expectedStatus, booking.getStatus(), "Booking status should be CANCELLED"),
                () -> assertEquals(capacity, flight.getCapacity(), "Flight capacity should remain unchanged"),
                () -> assertEquals(98, flight.getOccupiedSeats(), "Flight occupied seats should decrement by 1"),
                () -> assertEquals(2, flight.availableSeats(), "Flight available seats should increment by 1")
        );
    }
}