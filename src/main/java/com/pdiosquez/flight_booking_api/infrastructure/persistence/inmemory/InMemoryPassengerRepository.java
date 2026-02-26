package com.pdiosquez.flight_booking_api.infrastructure.persistence.inmemory;

import com.pdiosquez.flight_booking_api.domain.model.Passenger;
import com.pdiosquez.flight_booking_api.domain.repository.PassengerRepository;
import com.pdiosquez.flight_booking_api.domain.util.DomainValidation;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryPassengerRepository implements PassengerRepository {

    private final Map<Long, Passenger> database = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator        = new AtomicLong(1);

    @Override
    public Passenger save(Passenger passenger) {
        DomainValidation.notNull(passenger, "Cannot save a null passenger.");
        Passenger passengerToSave = passenger;

        if (passengerToSave.getId() == null) {
            Long newGeneratedId = idGenerator.getAndIncrement();
            passengerToSave = Passenger.fromPersistence(
                    newGeneratedId,
                    passenger.getName(),
                    passenger.getEmail()
            );
        }
        database.put(passengerToSave.getId(), passengerToSave);
        return passengerToSave;
    }

    @Override
    public Optional<Passenger> findById(Long passengerId) {
        DomainValidation.notNull(passengerId, "Passenger ID cannot be null when searching.");
        return Optional.ofNullable(database.get(passengerId));
    }
}