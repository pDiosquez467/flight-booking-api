package com.pdiosquez.flight_booking_api.infrastructure.rest.controller;

import com.pdiosquez.flight_booking_api.application.service.PassengerService;
import com.pdiosquez.flight_booking_api.domain.model.Passenger;
import com.pdiosquez.flight_booking_api.infrastructure.rest.dtos.request.PassengerRequest;
import com.pdiosquez.flight_booking_api.infrastructure.rest.dtos.response.PassengerResponse;
import com.pdiosquez.flight_booking_api.infrastructure.rest.mapper.PassengerRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/passengers")
public class PassengerController {

    private final PassengerService passengerService;
    private final PassengerRestMapper passengerRestMapper;

    public PassengerController(PassengerService passengerService, PassengerRestMapper passengerRestMapper) {
        this.passengerService = passengerService;
        this.passengerRestMapper = passengerRestMapper;
    }

    @PostMapping
    public ResponseEntity<PassengerResponse> createPassenger(
            @RequestBody @Valid PassengerRequest request) {
        Passenger created =
                passengerService.create(request.name(), request.email());

        PassengerResponse response =
                passengerRestMapper.toResponse(created);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{passengerId}")
    public ResponseEntity<PassengerResponse> findById(@PathVariable Long passengerId) {
        Passenger found =
                passengerService.findById(passengerId);

        PassengerResponse response =
                passengerRestMapper.toResponse(found);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PassengerResponse>> findAll() {
        List<Passenger> passengers = passengerService.findAll();
        List<PassengerResponse> response = passengers.stream()
                .map(passengerRestMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
}
