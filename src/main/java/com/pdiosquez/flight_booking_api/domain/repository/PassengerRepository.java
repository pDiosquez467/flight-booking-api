package com.pdiosquez.flight_booking_api.domain.repository;

import com.pdiosquez.flight_booking_api.domain.model.Passenger;

import java.util.Optional;

public interface PassengerRepository {
    Passenger save(Passenger passenger);

    Optional<Passenger> findById(Long passengerId);
}
