package com.pdiosquez.flight_booking_api.application.service;

import com.pdiosquez.flight_booking_api.domain.exception.FlightNotFoundException;
import com.pdiosquez.flight_booking_api.domain.model.Flight;
import com.pdiosquez.flight_booking_api.domain.repository.FlightRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public Flight create(String origin,
                         String destination,
                         int capacity,
                         LocalDateTime departureTime) {

        return flightRepository.save(
                Flight.create(
                        origin,
                        destination,
                        capacity,
                        departureTime
                )
        );
    }

    public Flight findById(Long flightId) {
        return flightRepository
                .findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException(flightId));
    }
}
