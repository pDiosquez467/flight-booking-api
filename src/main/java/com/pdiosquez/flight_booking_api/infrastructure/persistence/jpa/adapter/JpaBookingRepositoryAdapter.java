package com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.adapter;

import com.pdiosquez.flight_booking_api.domain.model.Booking;
import com.pdiosquez.flight_booking_api.domain.repository.BookingRepository;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.entity.BookingEntity;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.mapper.BookingMapper;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.repository.SpringDataBookingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaBookingRepositoryAdapter implements BookingRepository {

    private final SpringDataBookingRepository springDataRepository;
    private final BookingMapper bookingMapper;

    public JpaBookingRepositoryAdapter(SpringDataBookingRepository springDataRepository, BookingMapper bookingMapper) {
        this.springDataRepository = springDataRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public Booking save(Booking booking) {
        BookingEntity bookingEntity =
                springDataRepository.save(bookingMapper.toEntity(booking));
        return bookingMapper.toDomain(bookingEntity);
    }



    @Override
    public Optional<Booking> findById(Long bookingId) {
        return springDataRepository
                .findById(bookingId)
                .map(bookingMapper::toDomain);
    }

    @Override
    public List<Booking> findAll() {
        return springDataRepository
                .findAll()
                .stream()
                .map(bookingMapper::toDomain)
                .toList();
    }
}
