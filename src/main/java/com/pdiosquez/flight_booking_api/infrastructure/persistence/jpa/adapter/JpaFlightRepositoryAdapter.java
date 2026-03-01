package com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.adapter;

import com.pdiosquez.flight_booking_api.domain.model.Flight;
import com.pdiosquez.flight_booking_api.domain.repository.FlightRepository;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.entity.FlightEntity;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.mapper.FlightMapper;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.repository.SpringDataFlightRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaFlightRepositoryAdapter implements FlightRepository {

    private final SpringDataFlightRepository springDataRepository;
    private final FlightMapper flightMapper;

    public JpaFlightRepositoryAdapter(SpringDataFlightRepository springDataRepository, FlightMapper flightMapper) {
        this.springDataRepository = springDataRepository;
        this.flightMapper = flightMapper;
    }

    @Override
    public Flight save(Flight flight) {
        FlightEntity flightEntity = springDataRepository.save(flightMapper.toEntity(flight));

        return flightMapper.toDomain(flightEntity);
    }

    @Override
    public Optional<Flight> findById(Long flightId) {
        return springDataRepository.findById(flightId)
                .map(flightMapper::toDomain);
    }
}