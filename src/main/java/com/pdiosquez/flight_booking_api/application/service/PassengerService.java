package com.pdiosquez.flight_booking_api.application.service;

import com.pdiosquez.flight_booking_api.domain.exception.PassengerNotFoundException;
import com.pdiosquez.flight_booking_api.domain.model.Passenger;
import com.pdiosquez.flight_booking_api.domain.repository.PassengerRepository;
import org.springframework.stereotype.Service;

@Service
public class PassengerService {

    private final PassengerRepository passengerRepository;

    public PassengerService(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    public Passenger create(String name, String email) {
        return passengerRepository.save(
                Passenger.create(name, email)
        );
    }

    public Passenger findById(Long passengerId) {
        return passengerRepository
                .findById(passengerId)
                .orElseThrow(() -> new PassengerNotFoundException(passengerId));
    }
}
