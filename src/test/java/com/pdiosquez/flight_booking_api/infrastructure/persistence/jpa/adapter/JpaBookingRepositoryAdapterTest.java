package com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.adapter;

import com.pdiosquez.flight_booking_api.domain.model.Booking;
import com.pdiosquez.flight_booking_api.domain.model.BookingStatus;
import com.pdiosquez.flight_booking_api.domain.model.Flight;
import com.pdiosquez.flight_booking_api.domain.model.Passenger;
import com.pdiosquez.flight_booking_api.domain.repository.BookingRepository;
import com.pdiosquez.flight_booking_api.domain.repository.FlightRepository;
import com.pdiosquez.flight_booking_api.domain.repository.PassengerRepository;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.mapper.BookingMapper;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.mapper.FlightMapper;
import com.pdiosquez.flight_booking_api.infrastructure.persistence.jpa.mapper.PassengerMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({
        JpaBookingRepositoryAdapter.class,
        BookingMapper.class,
        JpaPassengerRepositoryAdapter.class,
        PassengerMapper.class,
        JpaFlightRepositoryAdapter.class,
        FlightMapper.class
})
class JpaBookingRepositoryAdapterTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Qualifier("flightAdapter")
    @Autowired
    private FlightRepository flightRepository;

    @Qualifier("passengerAdapter")
    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private EntityManager entityManager;

    private Booking booking;
    private Passenger passenger;
    private Flight flight;
    private LocalDateTime now;

    private Passenger aPassenger() {
        return Passenger.create(
                "John Doe",
                "john.doe@gmail.com"
        );
    }

    private Flight aFlight(LocalDateTime departureTime) {
        return Flight.create(
                "BUE",
                "MAD",
                100,
                departureTime
        );
    }

    private Booking aBooking(Passenger passenger, Flight flight, LocalDateTime createdAt) {
        return Booking.create(
                passenger,
                flight,
                createdAt
        );
    }

    @BeforeEach
    void setUp() {
        now = LocalDateTime.of(2026, 1, 1, 1, 10);
        passenger = passengerRepository.save(aPassenger());
        flight = flightRepository.save(aFlight(now));
        booking = aBooking(passenger, flight, now.minusDays(5));
    }

    @Test
    @DisplayName("save should persist booking and assign generated identifier")
    void save_shouldPersistBookingAndAssignGeneratedId_whenBookingIsValid() {
        Booking saved = bookingRepository.save(booking);

        assertThat(saved.getId()).isNotNull().isPositive();
        assertThat(saved.getPassenger()).isEqualTo(passenger);
        assertThat(saved.getFlight()).isEqualTo(flight);
        assertThat(saved.getCreatedAt()).isEqualTo(booking.getCreatedAt());
    }

    @Test
    @DisplayName("save should persist booking with CONFIRMED status by default")
    void save_shouldPersistWithConfirmedStatus_whenBookingIsCreated() {
        Booking saved = bookingRepository.save(booking);

        assertThat(saved.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    @DisplayName("findById should return persisted booking after persistence context is cleared")
    void findById_shouldReturnBooking_whenItExistsInDatabase() {
        Booking saved = bookingRepository.save(booking);

        entityManager.flush();
        entityManager.clear();

        Optional<Booking> found = bookingRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getPassenger()).isEqualTo(saved.getPassenger());
        assertThat(found.get().getFlight()).isEqualTo(saved.getFlight());
        assertThat(found.get().getCreatedAt()).isEqualTo(saved.getCreatedAt());
        assertThat(found.get().getStatus()).isEqualTo(saved.getStatus());
    }

    @Test
    @DisplayName("findById should return empty optional when identifier does not exist")
    void findById_shouldReturnEmpty_whenIdDoesNotExist() {
        Optional<Booking> found = bookingRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("save should throw exception when passenger is transient")
    void save_shouldThrowException_whenPassengerIsTransient() {
        Passenger unsavedPassenger = aPassenger();
        Booking invalidBooking = aBooking(unsavedPassenger, flight, now.minusDays(5));

        assertThatThrownBy(() -> bookingRepository.save(invalidBooking))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    @DisplayName("save should throw exception when flight is transient")
    void save_shouldThrowException_whenFlightIsTransient() {
        Flight unsavedFlight = aFlight(now);
        Booking invalidBooking = aBooking(passenger, unsavedFlight, now.minusDays(5));

        assertThatThrownBy(() -> bookingRepository.save(invalidBooking))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    @DisplayName("save should update booking status to CANCELLED when booking is cancelled")
    void save_shouldUpdateStatus_whenBookingIsCancelled() {
        Booking saved = bookingRepository.save(booking);

        saved.getFlight().reserveSeat(now);
        saved.cancel(now);

        bookingRepository.save(saved);

        entityManager.flush();
        entityManager.clear();

        Optional<Booking> found = bookingRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getPassenger()).isEqualTo(saved.getPassenger());
        assertThat(found.get().getFlight()).isEqualTo(saved.getFlight());
    }

    @Test
    @DisplayName("save should not create new record when updating existing booking")
    void save_shouldNotCreateNewRecord_whenUpdatingExistingBooking() {
        Booking saved = bookingRepository.save(booking);
        Long idBefore = saved.getId();

        saved.getFlight().reserveSeat(now);
        saved.cancel(now);

        bookingRepository.save(saved);

        entityManager.flush();
        entityManager.clear();

        Long count = entityManager
                .createQuery("SELECT COUNT(b) FROM BookingEntity b", Long.class)
                .getSingleResult();

        assertThat(count).isEqualTo(1L);
        assertThat(saved.getId()).isEqualTo(idBefore);
    }
}