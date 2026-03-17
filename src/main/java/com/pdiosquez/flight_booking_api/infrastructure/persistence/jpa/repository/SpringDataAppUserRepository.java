package com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.repository;

import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.entity.AppUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataAppUserRepository extends JpaRepository<AppUserEntity, Long> {
    Optional<AppUserEntity> findByEmail(String email);
}
