package com.pdiosquez.flight_booking_api.infrastructure.rest.controller;

import com.pdiosquez.flight_booking_api.application.service.BookingService;
import com.pdiosquez.flight_booking_api.domain.model.Booking;
import com.pdiosquez.flight_booking_api.infrastructure.rest.dtos.request.BookingRequest;
import com.pdiosquez.flight_booking_api.infrastructure.rest.dtos.response.BookingResponse;
import com.pdiosquez.flight_booking_api.infrastructure.rest.mapper.BookingRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingRestMapper bookingRestMapper;

    public BookingController(BookingService bookingService, BookingRestMapper bookingRestMapper) {
        this.bookingService = bookingService;
        this.bookingRestMapper = bookingRestMapper;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> create(@RequestBody @Valid BookingRequest request) {
        LocalDateTime now = LocalDateTime.now();

        Booking created = bookingService.createBooking(
                request.passengerId(),
                request.flightId(),
                now
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookingRestMapper.toResponse(created));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> findById(@PathVariable Long bookingId) {
        Booking found = bookingService.findById(bookingId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookingRestMapper.toResponse(found));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> findAll() {
        List<Booking> bookings = bookingService.getAllBookings();

        List<BookingResponse> response = bookings.stream()
                            .map(bookingRestMapper::toResponse)
                            .toList();

        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long bookingId) {
        LocalDateTime cancelTime = LocalDateTime.now();

        bookingService.cancelBooking(bookingId, cancelTime);

        return ResponseEntity
                .noContent()
                .build();
    }
}
