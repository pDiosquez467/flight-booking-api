package com.pdiosquez.flight_booking_api.application.service;

import com.pdiosquez.flight_booking_api.domain.exception.BookingNotFoundException;
import com.pdiosquez.flight_booking_api.domain.exception.FlightNotFoundException;
import com.pdiosquez.flight_booking_api.domain.exception.PassengerNotFoundException;
import com.pdiosquez.flight_booking_api.domain.model.Booking;
import com.pdiosquez.flight_booking_api.domain.model.Flight;
import com.pdiosquez.flight_booking_api.domain.model.Passenger;
import com.pdiosquez.flight_booking_api.domain.repository.BookingRepository;
import com.pdiosquez.flight_booking_api.domain.repository.FlightRepository;
import com.pdiosquez.flight_booking_api.domain.repository.PassengerRepository;
import com.pdiosquez.flight_booking_api.domain.util.DomainValidation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final FlightRepository flightRepository;

    public BookingService(BookingRepository bookingRepository, PassengerRepository passengerRepository, FlightRepository flightRepository) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.passengerRepository = passengerRepository;
    }

    @Transactional
    public Booking createBooking(Long passengerId, Long flightId, LocalDateTime currentTime) {
        DomainValidation.notNull(passengerId, "Passenger ID is required to create a booking.");
        DomainValidation.notNull(flightId, "Flight ID is required to create a booking.");
        DomainValidation.notNull(currentTime, "Current time is required to create a booking.");

        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new PassengerNotFoundException(passengerId));

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException(flightId));

        flight.reserveSeat(currentTime);

        Booking createdBooking = Booking.create(passenger, flight, currentTime);

        return bookingRepository.save(createdBooking);
    }

    @Transactional
    public void cancelBooking(Long bookingId, LocalDateTime currentTime) {
        DomainValidation.notNull(currentTime, "Current time is required to cancel a booking.");

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        booking.cancel(currentTime);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}
