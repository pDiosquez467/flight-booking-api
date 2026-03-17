package com.pdiosquez.flight_booking_api.domain.model;

import com.pdiosquez.flight_booking_api.domain.util.DomainValidation;

import java.util.Objects;

public class AppUser {

    private static final String EMAIL_REGEX = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

    private final Long id;
    private final String email;
    private final String passwordHash;
    private final Role role;

    private AppUser(Long id, String email, String passwordHash, Role role) {
        DomainValidation.notBlank(email, "Email cannot be null or blank.");
        DomainValidation.matches(email, EMAIL_REGEX, "Invalid email format.");
        DomainValidation.notBlank(passwordHash, "Password hash cannot be null or blank.");
        DomainValidation.notNull(role, "Role must be provided.");
        this.id           = id;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.role         = role;
    }

    public static AppUser create(String email, String passwordHash, Role role) {
        return new AppUser(
                null,
                email,
                passwordHash,
                role
        );
    }

    public static AppUser fromPersistence(Long id, String email, String passwordHash, Role role) {
        DomainValidation.notNull(id, "AppUser ID must be provided.");
        return new AppUser(
                id,
                email,
                passwordHash,
                role
        );
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppUser appUser)) return false;
        if (id == null || appUser.id == null) return false;
        return Objects.equals(id, appUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}
