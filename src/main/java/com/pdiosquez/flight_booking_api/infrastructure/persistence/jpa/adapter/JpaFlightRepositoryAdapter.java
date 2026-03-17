package com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.adapter;

import com.pdiosquez.flight_booking_api.domain.model.Flight;
import com.pdiosquez.flight_booking_api.domain.repository.FlightRepository;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.entity.FlightEntity;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.mapper.FlightEntityMapper;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.repository.SpringDataFlightRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("flightAdapter")
@Primary
public class JpaFlightRepositoryAdapter implements FlightRepository {

    private final SpringDataFlightRepository springDataRepository;
    private final FlightEntityMapper flightEntityMapper;

    public JpaFlightRepositoryAdapter(SpringDataFlightRepository springDataRepository, FlightEntityMapper flightEntityMapper) {
        this.springDataRepository = springDataRepository;
        this.flightEntityMapper = flightEntityMapper;
    }

    @Override
    public Flight save(Flight flight) {
        FlightEntity flightEntity = springDataRepository.save(flightEntityMapper.toEntity(flight));

        return flightEntityMapper.toDomain(flightEntity);
    }

    @Override
    public Optional<Flight> findById(Long flightId) {
        return springDataRepository.findById(flightId)
                .map(flightEntityMapper::toDomain);
    }

    @Override
    public List<Flight> findAll() {
        List<FlightEntity> flightEntities = springDataRepository.findAll();
        return flightEntities.stream()
                .map(flightEntityMapper::toDomain)
                .toList();
    }
}