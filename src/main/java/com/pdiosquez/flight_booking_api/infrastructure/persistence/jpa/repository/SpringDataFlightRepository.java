package com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.repository;

import com.pdiosquez.flight_booking_api.domain.model.Flight;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.entity.FlightEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataFlightRepository extends JpaRepository<FlightEntity, Long> {
}
