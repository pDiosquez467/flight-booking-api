package com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.adapter;

import com.pdiosquez.flight_booking_api.domain.model.AppUser;
import com.pdiosquez.flight_booking_api.domain.repository.AppUserRepository;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.entity.AppUserEntity;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.mapper.AppUserJpaMapper;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.repository.SpringDataAppUserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("appUserAdapter")
public class JpaAppUserRepositoryAdapter implements AppUserRepository {

    private final SpringDataAppUserRepository springDataRepository;
    private final AppUserJpaMapper mapper;

    public JpaAppUserRepositoryAdapter(SpringDataAppUserRepository springDataRepository, AppUserJpaMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AppUser save(AppUser appUser) {
        AppUserEntity entity = mapper.toEntity(appUser);
        AppUserEntity savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<AppUser> findById(Long id) {
        return springDataRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<AppUser> findByEmail(String email) {
        return springDataRepository.findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    public List<AppUser> findAll() {
        List<AppUserEntity> entities = springDataRepository.findAll();
        return entities.stream()
                .map(mapper::toDomain)
                .toList();
    }
}
