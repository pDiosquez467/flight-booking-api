package com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.adapter;

import com.pdiosquez.flight_booking_api.domain.model.Passenger;
import com.pdiosquez.flight_booking_api.domain.repository.PassengerRepository;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.entity.PassengerEntity;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.mapper.PassengerMapper;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.repository.SpringDataPassengerRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaPassengerRepositoryAdapter implements PassengerRepository {

    private final SpringDataPassengerRepository springDataRepository;
    private final PassengerMapper mapper;

    public JpaPassengerRepositoryAdapter(SpringDataPassengerRepository springDataRepository, PassengerMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Passenger save(Passenger passenger) {
        PassengerEntity entity = mapper.toEntity(passenger);

        PassengerEntity savedEntity = springDataRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Passenger> findById(Long passengerId) {
        return springDataRepository.findById(passengerId)
                .map(mapper::toDomain);
    }
}
