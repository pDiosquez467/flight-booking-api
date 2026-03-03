package com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.repository;

import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataBookingRepository extends JpaRepository<BookingEntity, Long> {
}
