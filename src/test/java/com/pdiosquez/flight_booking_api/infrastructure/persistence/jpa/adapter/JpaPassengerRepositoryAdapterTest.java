package com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.adapter;

import com.pdiosquez.flight_booking_api.domain.model.Passenger;
import com.pdiosquez.flight_booking_api.domain.repository.PassengerRepository;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.entity.PassengerEntity;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.mapper.PassengerMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
@Import({JpaPassengerRepositoryAdapter.class, PassengerMapper.class})
class JpaPassengerRepositoryAdapterTest {

    @Autowired
    private JpaPassengerRepositoryAdapter passengerRepository;

    @Autowired
    private EntityManager entityManager;
    private Passenger passenger;

    @BeforeEach
    void setUp() {
        passenger = Passenger.create(
                "John Doe",
                "john.doe@example.com"
        );
    }

    @Test
    @DisplayName("Should persist and retrieve passenger after clearing persistence context")
    void shouldPersistAndRetrieveAfterContextClear() {

        Passenger saved = passengerRepository.save(passenger);

        entityManager.flush();
        entityManager.clear();

        Optional<Passenger> found =
                passengerRepository.findById(saved.getId());

        assertThat(found).isPresent();

        assertThat(found.get())
                .satisfies(p -> {
                    assertThat(p.getId()).isEqualTo(saved.getId());
                    assertThat(p.getName()).isEqualTo(saved.getName());
                    assertThat(p.getEmail()).isEqualTo(saved.getEmail());
                });
    }

    @Test
    @DisplayName("Given non-existing ID, then should return empty Optional")
    void findById_WhenIdDoesNotExist_ShouldReturnEmpty() {

        Optional<Passenger> found =
                passengerRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should fail when persisting passenger with null name")
    void shouldFailWhenNameIsNull() {

        PassengerEntity invalid =
                PassengerEntity.of(null, "john.doe@example.com");

        assertThatThrownBy(() -> {
            entityManager.persist(invalid);
            entityManager.flush();
        }).isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should fail when persisting passenger with null email")
    void shouldFailWhenEmailIsNull() {

        PassengerEntity invalid =
                PassengerEntity.of("John Doe", null);

        assertThatThrownBy(() -> {
            entityManager.persist(invalid);
            entityManager.flush();
        }).isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should fail when rehydrating passenger with invalid email format")
    void shouldFailWhenRehydratingPassengerWithInvalidEmail() {

        PassengerEntity corrupted =
                PassengerEntity.of("John Doe", "invalid-email");

        entityManager.persist(corrupted);
        entityManager.flush();
        entityManager.clear();

        assertThatThrownBy(() ->
                passengerRepository.findById(corrupted.getId())
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email format");
    }
}