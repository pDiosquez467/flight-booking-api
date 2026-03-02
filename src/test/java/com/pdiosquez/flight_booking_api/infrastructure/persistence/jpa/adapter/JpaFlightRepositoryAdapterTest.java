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
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
@Import({JpaFlightRepositoryAdapter.class, FlightMapper.class})
class JpaFlightRepositoryAdapterTest {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private EntityManager entityManager;

    private Flight flight;

    @BeforeEach
    void setUp() {
        flight = Flight.create(
                "BUE",
                "MAD",
                100,
                LocalDateTime.of(2026, 1, 1, 1, 1, 10)
        );
    }

    @Test
    @DisplayName("Should persist flight and return generated ID")
    void save_shouldPersistFlightAndGenerateId() {

        Flight saved = flightRepository.save(flight);

        assertThat(saved.getId()).isNotNull().isPositive();
        assertThat(saved.getOrigin()).isEqualTo("BUE");
        assertThat(saved.getDestination()).isEqualTo("MAD");
        assertThat(saved.getCapacity()).isEqualTo(100);
        assertThat(saved.availableSeats()).isEqualTo(100);
    }

    @Test
    @DisplayName("Should persist and retrieve flight after clearing persistence context")
    void shouldPersistAndRetrieveAfterContextClear() {

        Flight saved = flightRepository.save(flight);

        entityManager.flush();
        entityManager.clear();

        Optional<Flight> found =
                flightRepository.findById(saved.getId());

        assertThat(found).isPresent();

        assertThat(found.get())
                .satisfies(f -> {
                    assertThat(f.getId()).isEqualTo(saved.getId());
                    assertThat(f.getOrigin()).isEqualTo(saved.getOrigin());
                    assertThat(f.getDestination()).isEqualTo(saved.getDestination());
                    assertThat(f.getCapacity()).isEqualTo(saved.getCapacity());
                    assertThat(f.availableSeats()).isEqualTo(saved.availableSeats());
                    assertThat(f.getDepartureTime()).isEqualTo(saved.getDepartureTime());
                });
    }

    @Test
    @DisplayName("Given non-existing ID, when findById is called, then should return empty Optional")
    void findById_whenIdDoesNotExist_ShouldReturnEmpty() {

        Optional<Flight> found =
                flightRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should fail when persisting flight with null origin")
    void shouldFailWhenOriginIsNull() {

        FlightEntity corrupted = FlightEntity.of(
                null,
                "MAD",
                100,
                0,
                LocalDateTime.of(2026,1,1,1,10)
        );

        assertThatThrownBy(() -> {
            entityManager.persist(corrupted);
            entityManager.flush();
        }).isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should fail when persisting flight with null destination")
    void shouldFailWhenDestinationIsNull() {

        FlightEntity corrupted = FlightEntity.of(
                "BUE",
                null,
                100,
                0,
                LocalDateTime.of(2026,1,1,1,10)
        );

        assertThatThrownBy(() -> {
            entityManager.persist(corrupted);
            entityManager.flush();
        }).isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should fail when persisting flight with null departure time")
    void shouldFailWhenDepartureTimeIsNull() {

        FlightEntity corrupted = FlightEntity.of(
                "BUE",
                "MAD",
                100,
                0,
                null
        );

        assertThatThrownBy(() -> {
            entityManager.persist(corrupted);
            entityManager.flush();
        }).isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should fail when rehydrating flight with occupied seats greater than capacity")
    void shouldFailWhenRehydratingFlightWithInvalidOccupiedSeats() {

        FlightEntity corrupted =
                FlightEntity.of(
                        "BUE",
                        "MAD",
                        100,
                        1000,
                        LocalDateTime.of(2026,1,1,1,10)
                );

        entityManager.persist(corrupted);
        entityManager.flush();
        entityManager.clear();

        assertThatThrownBy(() ->
                flightRepository.findById(corrupted.getId())
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Occupied seats");
    }

    @Test
    @DisplayName("Available seats should be capacity minus occupied seats after rehydration")
    void availableSeats_shouldBeCalculatedCorrectlyAfterRehydration() {

        FlightEntity entity =
                FlightEntity.of(
                        "BUE",
                        "MAD",
                        100,
                        20,
                        LocalDateTime.of(2026,1,1,1,10)
                );

        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();

        Flight rehydrated =
                flightRepository.findById(entity.getId()).orElseThrow();

        assertThat(rehydrated.availableSeats()).isEqualTo(80);
    }
}