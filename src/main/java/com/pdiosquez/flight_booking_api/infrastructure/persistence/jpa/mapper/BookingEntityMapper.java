package com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.mapper;

import com.pdiosquez.flight_booking_api.domain.model.Booking;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.entity.BookingEntity;
import org.springframework.stereotype.Component;

@Component
public class BookingEntityMapper {

    private final PassengerEntityMapper passengerEntityMapper;
    private final FlightEntityMapper flightEntityMapper;

    public BookingEntityMapper(PassengerEntityMapper passengerEntityMapper, FlightEntityMapper flightEntityMapper) {
        this.passengerEntityMapper = passengerEntityMapper;
        this.flightEntityMapper = flightEntityMapper;
    }

    public BookingEntity toEntity(Booking booking) {
        if (booking == null) return null;

        return new BookingEntity(
                booking.getId(),
                passengerEntityMapper.toEntity(booking.getPassenger()),
                flightEntityMapper.toEntity(booking.getFlight()),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }

    public Booking toDomain(BookingEntity bookingEntity) {
        if (bookingEntity == null) return null;

        return Booking.fromPersistence(
                bookingEntity.getId(),
                passengerEntityMapper.toDomain(bookingEntity.getPassengerEntity()),
                flightEntityMapper.toDomain(bookingEntity.getFlightEntity()),
                bookingEntity.getStatus(),
                bookingEntity.getCreatedAt()
        );
    }

}
