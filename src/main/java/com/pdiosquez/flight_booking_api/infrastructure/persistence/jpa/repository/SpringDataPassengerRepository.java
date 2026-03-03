package com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.repository;

import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.entity.PassengerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPassengerRepository extends JpaRepository<PassengerEntity, Long> {
}
