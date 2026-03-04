package com.pdiosquez.flight_booking_api.infrastructure.rest.controller;

import com.pdiosquez.flight_booking_api.application.service.FlightService;
import com.pdiosquez.flight_booking_api.domain.model.Flight;
import com.pdiosquez.flight_booking_api.infrastructure.rest.dtos.request.FlightRequest;
import com.pdiosquez.flight_booking_api.infrastructure.rest.dtos.response.FlightResponse;
import com.pdiosquez.flight_booking_api.infrastructure.rest.mapper.FlightMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/flights")
public class FlightController {

    private final FlightService flightService;
    private final FlightMapper flightMapper;

    public FlightController(FlightService flightService, FlightMapper flightMapper) {
        this.flightService = flightService;
        this.flightMapper = flightMapper;
    }

    @PostMapping
    public ResponseEntity<FlightResponse> create(@RequestBody @Valid FlightRequest request) {
        Flight saved = flightService.create(
                request.origin(),
                request.destination(),
                request.capacity(),
                request.departureTime()
        );

        FlightResponse response = flightMapper.toResponse(saved);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{flightId}")
    public ResponseEntity<FlightResponse> findById(@PathVariable Long flightId) {
        Flight found = flightService.findById(flightId);
        FlightResponse response = flightMapper.toResponse(found);

        return ResponseEntity.ok(response);
    }
}
