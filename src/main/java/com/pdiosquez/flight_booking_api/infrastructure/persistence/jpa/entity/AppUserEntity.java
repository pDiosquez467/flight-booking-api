package com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.entity;

import com.pdiosquez.flight_booking_api.domain.model.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(name = "email_address", unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private Role role;

    public static AppUserEntity of(String email, String passwordHash, Role role) {
        return of(null, email, passwordHash, role);
    }

    public static AppUserEntity of(Long id, String email, String passwordHash, Role role) {
        AppUserEntity entity = new AppUserEntity();
        entity.id           = id;
        entity.email        = email;
        entity.passwordHash = passwordHash;
        entity.role         = role;
        return entity;
    }
}
