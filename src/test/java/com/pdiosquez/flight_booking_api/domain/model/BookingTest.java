package com.pdiosquez.flight_booking_api.domain.model;

import com.pdiosquez.flight_booking_api.domain.exception.BookingAlreadyCancelledException;
import com.pdiosquez.flight_booking_api.domain.exception.BookingCancellationWindowClosedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Booking Domain Entity Tests")
class BookingTest {

    private static final Long BOOKING_ID = 23L;
    private static final Long PASSENGER_ID = 467L;
    private static final Long FLIGHT_ID = 101L;

    private LocalDateTime now() {
        return LocalDateTime.of(2026, 1, 1, 10, 0);
    }

    private Passenger passenger() {
        return Passenger.fromPersistence(
                PASSENGER_ID,
                "John Doe",
                "john.doe@example.com"
        );
    }

    private Flight futureFlight(int capacity, int occupiedSeats) {
        return Flight.fromPersistence(
                FLIGHT_ID,
                "BUE",
                "MAD",
                capacity,
                occupiedSeats,
                now().plusDays(5)
        );
    }

    private Flight pastFlight(int capacity, int occupiedSeats) {
        return Flight.fromPersistence(
                FLIGHT_ID,
                "BUE",
                "MAD",
                capacity,
                occupiedSeats,
                now().minusDays(5)
        );
    }

    @Test
    @DisplayName("Should create transient booking with CONFIRMED status")
    void shouldCreateBookingSuccessfully() {

        Booking booking = Booking.create(passenger(), futureFlight(100, 99), now());

        assertAll(
                () -> assertNull(booking.getId()),
                () -> assertEquals(BookingStatus.CONFIRMED, booking.getStatus()),
                () -> assertEquals(now(), booking.getCreatedAt())
        );
    }

    @Test
    @DisplayName("Should throw exception when passenger is null")
    void shouldThrowWhenPassengerIsNull() {

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> Booking.create(null, futureFlight(100, 99), now())
        );

        assertEquals("Passenger is required for a booking.", ex.getMessage());
    }

    @Test
    @DisplayName("Should reconstruct booking from persistence")
    void shouldReconstructBooking() {

        Booking booking = Booking.fromPersistence(
                BOOKING_ID,
                passenger(),
                futureFlight(100, 99),
                BookingStatus.CONFIRMED,
                now()
        );

        assertAll(
                () -> assertEquals(BOOKING_ID, booking.getId()),
                () -> assertEquals(BookingStatus.CONFIRMED, booking.getStatus())
        );
    }

    @Test
    @DisplayName("Should throw exception when reconstructing with null ID")
    void shouldThrowWhenFromPersistenceIdIsNull() {

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> Booking.fromPersistence(
                        null,
                        passenger(),
                        futureFlight(100, 99),
                        BookingStatus.CONFIRMED,
                        now()
                )
        );

        assertEquals("Booking ID is required for persistence reconstruction.", ex.getMessage());
    }

    @Test
    @DisplayName("Should cancel booking and release seat when rules are satisfied")
    void shouldCancelBookingSuccessfully() {

        Flight flight = futureFlight(100, 99);

        Booking booking = Booking.create(passenger(), flight, now());

        booking.cancel(now());

        assertAll(
                () -> assertEquals(BookingStatus.CANCELLED, booking.getStatus()),
                () -> assertEquals(98, flight.getOccupiedSeats())
        );
    }

    @Test
    @DisplayName("Should throw when cancelling already cancelled booking")
    void shouldThrowWhenAlreadyCancelled() {

        Booking booking = Booking.fromPersistence(
                BOOKING_ID,
                passenger(),
                futureFlight(100, 99),
                BookingStatus.CANCELLED,
                now()
        );

        BookingAlreadyCancelledException ex = assertThrows(
                BookingAlreadyCancelledException.class,
                () -> booking.cancel(now())
        );

        assertEquals(
                "Booking %d is already cancelled and cannot be cancelled again."
                        .formatted(BOOKING_ID),
                ex.getMessage()
        );
    }

    @Test
    @DisplayName("Should throw when cancelling after flight departure")
    void shouldThrowWhenFlightAlreadyDeparted() {

        Booking booking = Booking.fromPersistence(
                BOOKING_ID,
                passenger(),
                pastFlight(100, 99),
                BookingStatus.CONFIRMED,
                now()
        );

        BookingCancellationWindowClosedException ex = assertThrows(
                BookingCancellationWindowClosedException.class,
                () -> booking.cancel(now())
        );

        assertEquals(
                "Cannot cancel Booking %d because the flight has already departed."
                        .formatted(BOOKING_ID),
                ex.getMessage()
        );
    }
}