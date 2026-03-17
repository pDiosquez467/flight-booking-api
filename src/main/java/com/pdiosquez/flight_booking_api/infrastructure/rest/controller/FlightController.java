package com.pdiosquez.flight_booking_api.infrastructure.rest.controller;

import com.pdiosquez.flight_booking_api.application.service.FlightService;
import com.pdiosquez.flight_booking_api.domain.model.Flight;
import com.pdiosquez.flight_booking_api.infrastructure.rest.dtos.request.FlightRequest;
import com.pdiosquez.flight_booking_api.infrastructure.rest.dtos.response.FlightResponse;
import com.pdiosquez.flight_booking_api.infrastructure.rest.mapper.FlightRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flights")
public class FlightController {

    private final FlightService flightService;
    private final FlightRestMapper flightRestMapper;

    public FlightController(FlightService flightService, FlightRestMapper flightRestMapper) {
        this.flightService = flightService;
        this.flightRestMapper = flightRestMapper;
    }

    @PostMapping
    public ResponseEntity<FlightResponse> create(@RequestBody @Valid FlightRequest request) {
        Flight saved = flightService.create(
                request.origin(),
                request.destination(),
                request.capacity(),
                request.departureTime()
        );

        FlightResponse response = flightRestMapper.toResponse(saved);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{flightId}")
    public ResponseEntity<FlightResponse> findById(@PathVariable Long flightId) {
        Flight found = flightService.findById(flightId);
        FlightResponse response = flightRestMapper.toResponse(found);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FlightResponse>> findAll() {
        List<Flight> flights = flightService.findAll();
        List<FlightResponse> response = flights.stream()
                .map(flightRestMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
}
