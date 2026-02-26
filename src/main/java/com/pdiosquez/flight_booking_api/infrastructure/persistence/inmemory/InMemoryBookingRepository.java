package com.pdiosquez.flight_booking_api.infrastructure.persistence.inmemory;

import com.pdiosquez.flight_booking_api.domain.model.Booking;
import com.pdiosquez.flight_booking_api.domain.repository.BookingRepository;
import com.pdiosquez.flight_booking_api.domain.util.DomainValidation;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryBookingRepository implements BookingRepository {

    private final Map<Long, Booking> database = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator      = new AtomicLong(1);

    @Override
    public Booking save(Booking booking) {
        DomainValidation.notNull(booking, "Cannot save a null booking.");
        Booking bookingToSave = booking;

        if (bookingToSave.getId() == null) {
            Long newGeneratedId = idGenerator.getAndIncrement();
            bookingToSave = Booking.fromPersistence(
                    newGeneratedId,
                    booking.getPassenger(),
                    booking.getFlight(),
                    booking.getStatus(),
                    booking.getCreatedAt()
            );
        }
        database.put(bookingToSave.getId(), bookingToSave);
        return bookingToSave;
    }

    @Override
    public Optional<Booking> findById(Long bookingId) {
        DomainValidation.notNull(bookingId, "Booking ID cannot be null when searching.");
        return Optional.ofNullable(database.get(bookingId));
    }

    @Override
    public List<Booking> findAll() {
        return database.values().stream().toList();
    }
}