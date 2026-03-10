package com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.adapter;

import com.pdiosquez.flight_booking_api.domain.model.Booking;
import com.pdiosquez.flight_booking_api.domain.repository.BookingRepository;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.entity.BookingEntity;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.mapper.BookingEntityMapper;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.repository.SpringDataBookingRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class JpaBookingRepositoryAdapter implements BookingRepository {

    private final SpringDataBookingRepository springDataRepository;
    private final BookingEntityMapper bookingEntityMapper;

    public JpaBookingRepositoryAdapter(SpringDataBookingRepository springDataRepository, BookingEntityMapper bookingEntityMapper) {
        this.springDataRepository = springDataRepository;
        this.bookingEntityMapper = bookingEntityMapper;
    }

    @Override
    public Booking save(Booking booking) {
        BookingEntity bookingEntity =
                springDataRepository.save(bookingEntityMapper.toEntity(booking));
        return bookingEntityMapper.toDomain(bookingEntity);
    }

    @Override
    public Optional<Booking> findById(Long bookingId) {
        return springDataRepository
                .findById(bookingId)
                .map(bookingEntityMapper::toDomain);
    }

    @Override
    public List<Booking> findAll() {
        return springDataRepository
                .findAll()
                .stream()
                .map(bookingEntityMapper::toDomain)
                .toList();
    }
}
