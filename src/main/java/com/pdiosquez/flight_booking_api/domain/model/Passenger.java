package com.pdiosquez.flight_booking_api.domain.model;

import com.pdiosquez.flight_booking_api.domain.util.DomainValidation;
import java.util.Objects;

public class Passenger {

    private final Long id;
    private final String name;
    private final String email;

    private Passenger(Long id, String name, String email) {
        DomainValidation.notBlank(name, "Passenger name cannot be empty or blank.");
        DomainValidation.notBlank(email, "Passenger email cannot be empty or blank.");
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public static Passenger create(String name, String email) {
        return new Passenger(null, name, email);
    }

    public static Passenger fromPersistence(Long id, String name, String email) {
        DomainValidation.notNull(id, "Passenger ID must be provided.");
        return new Passenger(id, name, email);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Passenger passenger)) return false;
        return Objects.equals(id, passenger.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}