package com.pdiosquez.flight_booking_api.infrastructure.rest.controller;


import com.pdiosquez.flight_booking_api.application.service.PassengerService;
import com.pdiosquez.flight_booking_api.domain.exception.PassengerNotFoundException;
import com.pdiosquez.flight_booking_api.domain.model.Passenger;
import com.pdiosquez.flight_booking_api.infrastructure.rest.dtos.request.PassengerRequest;
import com.pdiosquez.flight_booking_api.infrastructure.rest.mapper.PassengerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PassengerController.class)
@Import({PassengerMapper.class})
class PassengerControllerTest {

    private static final String BASE_PATH = "/api/v1/passengers";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PassengerService passengerService;

    private Passenger passenger;
    private PassengerRequest validRequest;

    @BeforeEach
    void setUp() {
        passenger = Passenger.fromPersistence(
                1L,
                "John Doe",
                "john.doe@example.com"
        );

        validRequest = new PassengerRequest(
                passenger.getName(),
                passenger.getEmail()
        );
    }

    @Test
    @DisplayName("Given valid payload when create is called then returns 201 Created")
    void givenValidPayload_whenCreate_thenReturns201() throws Exception {

        when(passengerService.create(
                eq("John Doe"),
                eq("john.doe@example.com")
        )).thenReturn(passenger);

        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(passenger.getId()))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @DisplayName("Given existing ID when findById is called then returns 200 OK with passenger data")
    void givenExistingId_whenFindById_thenReturns200() throws Exception {

        Long passengerId = 1L;

        when(passengerService.findById(passengerId))
                .thenReturn(passenger);

        mockMvc.perform(get(BASE_PATH + "/{id}", passengerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(passenger.getId()))
                .andExpect(jsonPath("$.name").value(passenger.getName()))
                .andExpect(jsonPath("$.email").value(passenger.getEmail()));
    }

    @Test
    @DisplayName("Given non existing ID when findById is called then returns 404 Not Found")
    void givenNonExistingId_whenFindById_thenReturns404() throws Exception {

        Long nonExistingId = 999L;

        when(passengerService.findById(nonExistingId))
                .thenThrow(new PassengerNotFoundException(nonExistingId));

        mockMvc.perform(get(BASE_PATH + "/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PASSENGER_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Given invalid payload when create is called then returns 400 Bad Request and does not invoke service")
    void givenInvalidPayload_whenCreate_thenReturns400() throws Exception {

        PassengerRequest invalidRequest = new PassengerRequest(
                "",
                "john.doe@example.com"
        );

        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST_VALIDATION_ERROR"));

        Mockito.verify(passengerService, never())
                .create(anyString(), anyString());
    }
}