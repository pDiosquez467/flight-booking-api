package com.pdiosquez.flight_booking_api.infrastructure.persistence.inmemory;

import com.pdiosquez.flight_booking_api.domain.model.Flight;
import com.pdiosquez.flight_booking_api.domain.repository.FlightRepository;
import com.pdiosquez.flight_booking_api.domain.util.DomainValidation;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryFlightRepository implements FlightRepository {

    private final Map<Long, Flight> database = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator     = new AtomicLong(1);

    @Override
    public Flight save(Flight flight) {
        DomainValidation.notNull(flight, "Cannot save a null flight.");
        Flight flightToSave = flight;

        if (flightToSave.getId() == null) {
            Long newGeneratedId = idGenerator.getAndIncrement();
            flightToSave = Flight.fromPersistence(
                    newGeneratedId,
                    flight.getOrigin(),
                    flight.getDestination(),
                    flight.getCapacity(),
                    flight.getOccupiedSeats(),
                    flight.getDepartureTime()
            );
        }
        database.put(flightToSave.getId(), flightToSave);
        return flightToSave;
    }

    @Override
    public Optional<Flight> findById(Long flightId) {
        DomainValidation.notNull(flightId, "Flight ID cannot be null when searching.");
        return Optional.ofNullable(database.get(flightId));
    }
}