package com.pdiosquez.flight_booking_api.application.service;

import com.pdiosquez.flight_booking_api.domain.exception.DomainException;
import com.pdiosquez.flight_booking_api.domain.exception.FlightNotFoundException;
import com.pdiosquez.flight_booking_api.domain.exception.PassengerNotFoundException;
import com.pdiosquez.flight_booking_api.domain.model.Booking;
import com.pdiosquez.flight_booking_api.domain.model.BookingStatus;
import com.pdiosquez.flight_booking_api.domain.model.Flight;
import com.pdiosquez.flight_booking_api.domain.model.Passenger;
import com.pdiosquez.flight_booking_api.domain.repository.BookingRepository;
import com.pdiosquez.flight_booking_api.domain.repository.FlightRepository;
import com.pdiosquez.flight_booking_api.domain.repository.PassengerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Booking Service Application Tests")
class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private PassengerRepository passengerRepository;
    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    @DisplayName("Should create booking and reserve seat when passenger and flight exist")
    void shouldCreateBookingAndReserveSeat_whenPassengerAndFlightExist() {
        // Given
        Long passengerId = 101L;
        Long flightId = 467L;
        LocalDateTime fixedNow = LocalDateTime.of(2026, 1, 1, 10, 0);

        Passenger passenger = Passenger.fromPersistence(
                passengerId,
                "John Doe",
                "john.doe@example.com"
        );

        Flight flight = Flight.fromPersistence(
                flightId,
                "BUE",
                "MAD",
                100,
                0,
                fixedNow.plusDays(5)
        );

        when(passengerRepository.findById(passengerId))
                .thenReturn(Optional.of(passenger));

        when(flightRepository.findById(flightId))
                .thenReturn(Optional.of(flight));

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Booking result = bookingService.createBooking(passengerId, flightId, fixedNow);

        // Then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(passenger, result.getPassenger()),
                () -> assertEquals(flight, result.getFlight())
        );

        verify(passengerRepository).findById(passengerId);
        verify(flightRepository).findById(flightId);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should throw PassengerNotFoundException when passenger does not exist")
    void shouldThrowPassengerNotFoundException_whenPassengerDoesNotExist() {
        // Given
        Long passengerId = 101L;
        Long flightId = 467L;
        LocalDateTime fixedNow = LocalDateTime.of(2026, 1, 1, 10, 0);

        when(passengerRepository.findById(passengerId))
                .thenReturn(Optional.empty());

        // When / Then
        PassengerNotFoundException exception = assertThrows(
                PassengerNotFoundException.class,
                () -> bookingService.createBooking(passengerId, flightId, fixedNow)
        );

        assertEquals(
                "Passenger with ID %d not found.".formatted(passengerId),
                exception.getMessage()
        );

        verify(passengerRepository).findById(passengerId);
        verifyNoInteractions(flightRepository);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Should throw FlightNotFoundException when flight does not exist")
    void shouldThrowFlightNotFoundException_whenFlightDoesNotExist() {
        // Given
        Long passengerId = 101L;
        Long flightId = 467L;
        LocalDateTime fixedNow = LocalDateTime.of(2026, 1, 1, 10, 0);

        Passenger passenger = Passenger.fromPersistence(
                passengerId,
                "John Doe",
                "john.doe@example.com"
        );

        when(passengerRepository.findById(passengerId))
                .thenReturn(Optional.of(passenger));

        when(flightRepository.findById(flightId))
                .thenReturn(Optional.empty());

        // When / Then
        FlightNotFoundException exception = assertThrows(
                FlightNotFoundException.class,
                () -> bookingService.createBooking(passengerId, flightId, fixedNow)
        );

        assertEquals(
                "Flight with ID %d not found.".formatted(flightId),
                exception.getMessage()
        );

        verify(passengerRepository).findById(passengerId);
        verify(flightRepository).findById(flightId);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Should cancel booking and release seat when booking exists")
    void shouldCancelBookingAndReleaseSeat_whenBookingExists() {
        // Given
        Long bookingId = 23L;
        LocalDateTime fixedNow = LocalDateTime.of(2026, 1, 1, 10, 0);

        Passenger passenger = Passenger.fromPersistence(
                467L,
                "John Doe",
                "john.doe@example.com"
        );

        Flight flight = Flight.fromPersistence(
                101L,
                "BUE",
                "MAD",
                100,
                1,
                fixedNow.plusDays(5)
        );

        Booking booking = Booking.fromPersistence(
                bookingId,
                passenger,
                flight,
                BookingStatus.CONFIRMED,
                fixedNow
        );

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        // When
        bookingService.cancelBooking(bookingId, fixedNow);

        // Then
        assertAll(
                () -> assertEquals(BookingStatus.CANCELLED, booking.getStatus()),
                () -> assertEquals(0, flight.getOccupiedSeats())
        );

        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository, never()).save(any());
        verifyNoInteractions(passengerRepository);
        verifyNoInteractions(flightRepository);
    }
}