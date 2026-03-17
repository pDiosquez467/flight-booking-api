package com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.mapper;

import com.pdiosquez.flight_booking_api.domain.model.AppUser;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.entity.AppUserEntity;
import org.springframework.stereotype.Component;

@Component
public class AppUserJpaMapper {

    public AppUserEntity toEntity(AppUser appUser) {
        if (appUser == null) return null;
        return AppUserEntity.of(
                appUser.getId(),
                appUser.getEmail(),
                appUser.getPasswordHash(),
                appUser.getRole()
        );
    }

    public AppUser toDomain(AppUserEntity appUserEntity) {
        if (appUserEntity == null) return null;
        return AppUser.fromPersistence(
                appUserEntity.getId(),
                appUserEntity.getEmail(),
                appUserEntity.getPasswordHash(),
                appUserEntity.getRole()
        );
    }
}
