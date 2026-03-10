package com.pdiosquez.flight_booking_api.infrastructure.rest.mapper;

import com.pdiosquez.flight_booking_api.domain.model.Booking;
import com.pdiosquez.flight_booking_api.infrastructure.rest.dtos.response.BookingResponse;
import org.springframework.stereotype.Component;

@Component
public class BookingRestMapper {

    private final PassengerRestMapper passengerRestMapper;
    private final FlightRestMapper flightRestMapper;

    public BookingRestMapper(PassengerRestMapper passengerRestMapper, FlightRestMapper flightRestMapper) {
        this.passengerRestMapper = passengerRestMapper;
        this.flightRestMapper = flightRestMapper;
    }

    public BookingResponse toResponse(Booking booking) {
        if (booking == null) {
            return null;
        }

        return new BookingResponse(
                booking.getId(),
                booking.getStatus(),
                passengerRestMapper.toResponse(booking.getPassenger()),
                flightRestMapper.toResponse(booking.getFlight())
        );
    }
}
