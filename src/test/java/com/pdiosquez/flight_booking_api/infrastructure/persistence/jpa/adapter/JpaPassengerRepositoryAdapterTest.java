package com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.adapter;

import com.pdiosquez.flight_booking_api.domain.model.Passenger;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.entity.PassengerEntity;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.mapper.PassengerMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    @DisplayName("save should persist passenger and allow retrieval after clearing persistence context")
    void save_shouldPersistAndRetrievePassenger_whenPassengerIsValid() {
        Passenger saved = passengerRepository.save(passenger);

        entityManager.flush();
        entityManager.clear();

        Optional<Passenger> found = passengerRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getName()).isEqualTo(saved.getName());
        assertThat(found.get().getEmail()).isEqualTo(saved.getEmail());
    }

    @Test
    @DisplayName("findById should return empty optional when passenger does not exist")
    void findById_shouldReturnEmpty_whenIdDoesNotExist() {
        Optional<Passenger> found = passengerRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("findById should throw exception when rehydrated passenger contains invalid email format")
    void findById_shouldThrowException_whenRehydratedPassengerHasInvalidEmail() {
        PassengerEntity corrupted = PassengerEntity.of(
                "John Doe",
                "invalid-email"
        );

        entityManager.persist(corrupted);
        entityManager.flush();
        entityManager.clear();

        assertThatThrownBy(() -> passengerRepository.findById(corrupted.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email format");
    }
}