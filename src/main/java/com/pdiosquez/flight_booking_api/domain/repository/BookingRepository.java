package com.pdiosquez.flight_booking_api.domain.repository;

import com.pdiosquez.flight_booking_api.domain.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    Booking save(Booking booking);

    Optional<Booking> findById(Long bookingId);

    List<Booking> findAll();
}
