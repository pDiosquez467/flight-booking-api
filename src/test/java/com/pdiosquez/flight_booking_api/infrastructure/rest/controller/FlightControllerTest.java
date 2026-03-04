package com.pdiosquez.flight_booking_api.infrastructure.rest.controller;

import com.pdiosquez.flight_booking_api.application.service.FlightService;
import com.pdiosquez.flight_booking_api.domain.exception.FlightNotFoundException;
import com.pdiosquez.flight_booking_api.domain.model.Flight;
import com.pdiosquez.flight_booking_api.infrastructure.rest.dtos.request.FlightRequest;
import com.pdiosquez.flight_booking_api.infrastructure.rest.mapper.FlightMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FlightController.class)
@Import(FlightMapper.class)
class FlightControllerTest {

    private static final String BASE_PATH = "/api/v1/flights";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private FlightService flightService;

    private LocalDateTime departureTime;
    private Flight flight;
    private FlightRequest validRequest;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        departureTime = now.plusDays(5);

        flight = Flight.fromPersistence(
                467L,
                "BUE",
                "MAD",
                100,
                99,
                departureTime
        );

        validRequest = new FlightRequest(
                "BUE",
                "MAD",
                100,
                departureTime
        );
    }

    @Test
    @DisplayName("POST /api/v1/flights - returns 201 Created with flight data when payload is valid")
    void givenValidPayload_whenCreate_thenReturn201() throws Exception {
        when(flightService.create("BUE", "MAD", 100, departureTime))
                .thenReturn(flight);

        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(467L))
                .andExpect(jsonPath("$.origin").value("BUE"))
                .andExpect(jsonPath("$.destination").value("MAD"))
                .andExpect(jsonPath("$.capacity").value(100));

        verify(flightService).create("BUE", "MAD", 100, departureTime);
        verifyNoMoreInteractions(flightService);
    }

    @Test
    @DisplayName("GET /api/v1/flights/{id} - returns 200 OK with flight data when ID exists")
    void givenExistingId_whenFindById_thenReturns200() throws Exception {
        long flightId = 467L;

        when(flightService.findById(flightId))
                .thenReturn(flight);

        mockMvc.perform(get(BASE_PATH + "/{id}", flightId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(467L))
                .andExpect(jsonPath("$.origin").value("BUE"))
                .andExpect(jsonPath("$.destination").value("MAD"))
                .andExpect(jsonPath("$.capacity").value(100));

        verify(flightService).findById(flightId);
        verifyNoMoreInteractions(flightService);
    }

    @Test
    @DisplayName("GET /api/v1/flights/{id} - returns 404 Not Found when ID does not exist")
    void givenNonExistingId_whenFindById_thenReturns404() throws Exception {
        long nonExistingId = 999L;

        when(flightService.findById(nonExistingId))
                .thenThrow(new FlightNotFoundException(nonExistingId));

        mockMvc.perform(get(BASE_PATH + "/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("FLIGHT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());

        verify(flightService).findById(nonExistingId);
        verifyNoMoreInteractions(flightService);
    }

    @Test
    @DisplayName("POST /api/v1/flights - returns 400 Bad Request when payload is invalid")
    void givenInvalidPayload_whenCreate_thenReturn400() throws Exception {
        FlightRequest invalidRequest = new FlightRequest(
                "",
                "MAD",
                100,
                departureTime
        );

        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST_VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(flightService);
    }
}