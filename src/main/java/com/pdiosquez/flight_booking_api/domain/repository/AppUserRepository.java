package com.pdiosquez.flight_booking_api.domain.repository;

import com.pdiosquez.flight_booking_api.domain.model.AppUser;

import java.util.Optional;

public interface AppUserRepository {
    AppUser save(AppUser appUser);

    Optional<AppUser> findById(Long id);

    Optional<AppUser> findByEmail(String email);
}
