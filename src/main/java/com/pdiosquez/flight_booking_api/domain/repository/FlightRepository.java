package com.pdiosquez.flight_booking_api.domain.repository;

import com.pdiosquez.flight_booking_api.domain.model.Flight;

import java.util.Optional;

public interface FlightRepository {
    Flight save(Flight flight);

    Optional<Flight> findById(Long flightId);
}
