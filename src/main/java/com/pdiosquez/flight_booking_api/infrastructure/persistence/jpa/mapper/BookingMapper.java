package com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.mapper;

import com.pdiosquez.flight_booking_api.domain.model.Booking;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.entity.BookingEntity;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    private final PassengerMapper passengerMapper;
    private final FlightMapper flightMapper;

    public BookingMapper(PassengerMapper passengerMapper, FlightMapper flightMapper) {
        this.passengerMapper = passengerMapper;
        this.flightMapper    = flightMapper;
    }

    public BookingEntity toEntity(Booking booking) {
        if (booking == null) return null;

        return new BookingEntity(
                booking.getId(),
                passengerMapper.toEntity(booking.getPassenger()),
                flightMapper.toEntity(booking.getFlight()),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }

    public Booking toDomain(BookingEntity bookingEntity) {
        if (bookingEntity == null) return null;

        return Booking.fromPersistence(
                bookingEntity.getId(),
                passengerMapper.toDomain(bookingEntity.getPassengerEntity()),
                flightMapper.toDomain(bookingEntity.getFlightEntity()),
                bookingEntity.getStatus(),
                bookingEntity.getCreatedAt()
        );
    }

}
