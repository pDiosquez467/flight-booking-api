package com.pdiosquez.flight_booking_api.infrastructure.rest.controller;

import com.pdiosquez.flight_booking_api.application.service.BookingService;
import com.pdiosquez.flight_booking_api.domain.exception.BookingNotFoundException;
import com.pdiosquez.flight_booking_api.domain.model.Booking;
import com.pdiosquez.flight_booking_api.domain.model.BookingStatus;
import com.pdiosquez.flight_booking_api.domain.model.Flight;
import com.pdiosquez.flight_booking_api.domain.model.Passenger;
import com.pdiosquez.flight_booking_api.infrastructure.rest.dtos.request.BookingRequest;
import com.pdiosquez.flight_booking_api.infrastructure.rest.error.GlobalExceptionHandler;
import com.pdiosquez.flight_booking_api.infrastructure.rest.mapper.BookingMapper;
import com.pdiosquez.flight_booking_api.infrastructure.rest.mapper.FlightMapper;
import com.pdiosquez.flight_booking_api.infrastructure.rest.mapper.PassengerMapper;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@Import({
        BookingMapper.class,
        PassengerMapper.class,
        FlightMapper.class,
        GlobalExceptionHandler.class
})
class BookingControllerTest {

    private static final String BASE_PATH = "/api/v1/bookings";

    private static final Long PASSENGER_ID = 101L;
    private static final Long FLIGHT_ID = 467L;
    private static final Long BOOKING_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookingService bookingService;

    private Booking booking;
    private BookingRequest validRequest;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime departureTime = now.plusDays(5);

        Passenger passenger = Passenger.fromPersistence(
                PASSENGER_ID,
                "John Doe",
                "john.doe@example.com"
        );

        Flight flight = Flight.fromPersistence(
                FLIGHT_ID,
                "BUE",
                "MAD",
                100,
                99,
                departureTime
        );

        booking = Booking.fromPersistence(
                BOOKING_ID,
                passenger,
                flight,
                BookingStatus.CONFIRMED,
                now
        );

        validRequest = new BookingRequest(PASSENGER_ID, FLIGHT_ID);
    }

    @Test
    @DisplayName("POST /api/v1/bookings - returns 201 Created with booking data when payload is valid")
    void givenValidPayload_whenCreate_thenReturns201() throws Exception {
        when(bookingService.createBooking(eq(PASSENGER_ID), eq(FLIGHT_ID), any(LocalDateTime.class)))
                .thenReturn(booking);

        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").value(BOOKING_ID))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.passenger.id").value(PASSENGER_ID))
                .andExpect(jsonPath("$.passenger.name").value("John Doe"))
                .andExpect(jsonPath("$.passenger.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.flight.id").value(FLIGHT_ID))
                .andExpect(jsonPath("$.flight.origin").value("BUE"))
                .andExpect(jsonPath("$.flight.destination").value("MAD"));

        verify(bookingService).createBooking(eq(PASSENGER_ID), eq(FLIGHT_ID), any(LocalDateTime.class));
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    @DisplayName("GET /api/v1/bookings/{id} - returns 200 OK with booking data when ID exists")
    void givenValidId_whenFindById_thenReturns200() throws Exception {
        when(bookingService.findById(BOOKING_ID)).thenReturn(booking);

        mockMvc.perform(get(BASE_PATH + "/{id}", BOOKING_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(BOOKING_ID))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.passenger.id").value(PASSENGER_ID))
                .andExpect(jsonPath("$.flight.id").value(FLIGHT_ID));

        verify(bookingService).findById(BOOKING_ID);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    @DisplayName("GET /api/v1/bookings/{id} - returns 404 Not Found when ID does not exist")
    void givenNonExistingId_whenFindById_thenReturns404() throws Exception {
        Long nonExistingId = 999L;

        when(bookingService.findById(nonExistingId))
                .thenThrow(new BookingNotFoundException(nonExistingId));

        mockMvc.perform(get(BASE_PATH + "/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("BOOKING_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());

        verify(bookingService).findById(nonExistingId);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    @DisplayName("POST /api/v1/bookings - returns 400 Bad Request when payload is invalid")
    void givenInvalidPayload_whenCreate_thenReturns400() throws Exception {
        BookingRequest invalidRequest = new BookingRequest(null, FLIGHT_ID);

        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST_VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(bookingService);
    }

    @Test
    @DisplayName("PATCH /api/v1/bookings/{id}/cancel - returns 202 Accepted when booking is cancelled")
    void givenExistingId_whenCancel_thenReturns202() throws Exception {
        mockMvc.perform(patch(BASE_PATH + "/{id}/cancel", BOOKING_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(bookingService).cancelBooking(eq(BOOKING_ID), any(LocalDateTime.class));
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    @DisplayName("PATCH /api/v1/bookings/{id}/cancel - returns 404 Not Found when booking does not exist")
    void givenNonExistingId_whenCancel_thenReturns404() throws Exception {
        Long nonExistingId = 999L;

        doThrow(new BookingNotFoundException(nonExistingId))
                .when(bookingService).cancelBooking(eq(nonExistingId), any(LocalDateTime.class));

        mockMvc.perform(patch(BASE_PATH + "/{id}/cancel", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("BOOKING_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());

        verify(bookingService).cancelBooking(eq(nonExistingId), any(LocalDateTime.class));
        verifyNoMoreInteractions(bookingService);
    }
}