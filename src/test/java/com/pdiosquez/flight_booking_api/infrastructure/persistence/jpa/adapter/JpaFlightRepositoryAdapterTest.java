package com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.adapter;

import com.pdiosquez.flight_booking_api.domain.model.Flight;
import com.pdiosquez.flight_booking_api.domain.repository.FlightRepository;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.entity.FlightEntity;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.mapper.FlightMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({JpaFlightRepositoryAdapter.class, FlightMapper.class})
class JpaFlightRepositoryAdapterTest {

    @Autowired
    @Qualifier("flightAdapter")
    private FlightRepository flightRepository;

    @Autowired
    private EntityManager entityManager;

    private Flight flight;
    private LocalDateTime departureTime;

    @BeforeEach
    void setUp() {
        departureTime = LocalDateTime.of(2026, 1, 1, 1, 1, 10);
        flight = Flight.create(
                "BUE",
                "MAD",
                100,
                departureTime
        );
    }

    @Test
    @DisplayName("save should persist flight and assign generated identifier")
    void save_shouldPersistFlightAndAssignGeneratedId_whenFlightIsValid() {
        Flight saved = flightRepository.save(flight);

        assertThat(saved.getId()).isNotNull().isPositive();
        assertThat(saved.getOrigin()).isEqualTo("BUE");
        assertThat(saved.getDestination()).isEqualTo("MAD");
        assertThat(saved.getCapacity()).isEqualTo(100);
        assertThat(saved.availableSeats()).isEqualTo(100);
        assertThat(saved.getDepartureTime()).isEqualTo(departureTime);
    }

    @Test
    @DisplayName("findById should return persisted flight after clearing persistence context")
    void findById_shouldReturnFlight_whenFlightExists() {
        Flight saved = flightRepository.save(flight);

        entityManager.flush();
        entityManager.clear();

        Optional<Flight> found = flightRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getOrigin()).isEqualTo(saved.getOrigin());
        assertThat(found.get().getDestination()).isEqualTo(saved.getDestination());
        assertThat(found.get().getCapacity()).isEqualTo(saved.getCapacity());
        assertThat(found.get().availableSeats()).isEqualTo(saved.availableSeats());
        assertThat(found.get().getDepartureTime()).isEqualTo(saved.getDepartureTime());
    }

    @Test
    @DisplayName("findById should return empty optional when flight does not exist")
    void findById_shouldReturnEmpty_whenIdDoesNotExist() {
        Optional<Flight> found = flightRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("findById should throw exception when rehydrated flight contains invalid occupied seats")
    void findById_shouldThrowException_whenRehydratedFlightHasInvalidOccupiedSeats() {
        FlightEntity corrupted = FlightEntity.of(
                "BUE",
                "MAD",
                100,
                1000,
                departureTime
        );

        entityManager.persist(corrupted);
        entityManager.flush();
        entityManager.clear();

        assertThatThrownBy(() -> flightRepository.findById(corrupted.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Occupied seats");
    }

    @Test
    @DisplayName("availableSeats should be calculated correctly after entity rehydration")
    void findById_shouldCalculateAvailableSeatsCorrectly_whenFlightIsRehydrated() {
        FlightEntity entity = FlightEntity.of(
                "BUE",
                "MAD",
                100,
                20,
                departureTime
        );

        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();

        Flight rehydrated = flightRepository.findById(entity.getId()).orElseThrow();

        assertThat(rehydrated.availableSeats()).isEqualTo(80);
    }
}