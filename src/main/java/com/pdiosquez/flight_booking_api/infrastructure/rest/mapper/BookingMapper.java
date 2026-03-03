package com.pdiosquez.flight_booking_api.infrastructure.rest.mapper;

import com.pdiosquez.flight_booking_api.domain.model.Booking;
import com.pdiosquez.flight_booking_api.infrastructure.rest.dtos.response.BookingResponse;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    private final PassengerMapper passengerMapper;
    private final FlightMapper flightMapper;

    public BookingMapper(PassengerMapper passengerMapper, FlightMapper flightMapper) {
        this.passengerMapper = passengerMapper;
        this.flightMapper = flightMapper;
    }

    public BookingResponse toResponse(Booking booking) {
        if (booking == null) {
            return null;
        }

        return new BookingResponse(
                booking.getId(),
                booking.getStatus(),
                passengerMapper.toResponse(booking.getPassenger()),
                flightMapper.toResponse(booking.getFlight())
        );
    }
}
