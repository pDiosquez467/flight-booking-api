package com.pdiosquez.flight_booking_api.infrastructure.rest.controller;

import com.pdiosquez.flight_booking_api.application.service.PassengerService;
import com.pdiosquez.flight_booking_api.domain.model.Passenger;
import com.pdiosquez.flight_booking_api.infrastructure.rest.dtos.request.PassengerRequest;
import com.pdiosquez.flight_booking_api.infrastructure.rest.dtos.response.PassengerResponse;
import com.pdiosquez.flight_booking_api.infrastructure.rest.mapper.PassengerMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/passengers")
public class PassengerController {

    private final PassengerService passengerService;
    private final PassengerMapper passengerMapper;

    public PassengerController(PassengerService passengerService, PassengerMapper passengerMapper) {
        this.passengerService = passengerService;
        this.passengerMapper  = passengerMapper;
    }

    @PostMapping
    public ResponseEntity<PassengerResponse> createPassenger(
            @RequestBody @Valid PassengerRequest request) {
        Passenger created =
                passengerService.create(request.name(), request.email());

        PassengerResponse response =
                passengerMapper.toResponse(created);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{passengerId}")
    public ResponseEntity<PassengerResponse> findById(@PathVariable Long passengerId) {
        Passenger found =
                passengerService.findById(passengerId);

        PassengerResponse response =
                passengerMapper.toResponse(found);

        return ResponseEntity.ok(response);
    }
}
