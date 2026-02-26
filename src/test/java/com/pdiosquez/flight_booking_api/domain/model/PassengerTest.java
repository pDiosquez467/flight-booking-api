package com.pdiosquez.flight_booking_api.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Passenger Domain Entity Tests")
class PassengerTest {

    @Test
    @DisplayName("Given valid inputs, when create() is called, then a transient passenger is instantiated")
    void givenValidInputs_whenCreate_thenTransientPassengerInstantiated() {
        String validName  = "John Doe";
        String validEmail = "john.doe@example.com";

        Passenger passenger = Passenger.create(validName, validEmail);

        assertAll(
                "Verify newly created passenger state",
                () -> assertNull(passenger.getId(), "A newly created passenger must have a null ID"),
                () -> assertEquals(validName, passenger.getName(), "The name should match the provided valid input"),
                () -> assertEquals(validEmail, passenger.getEmail(), "The email should match the provided valid input")
        );
    }

    @Test
    @DisplayName("Given a blank name, when create() is called, then it should throw an IllegalArgumentException")
    void givenBlankName_whenCreate_thenThrowsException() {
        String invalidBlankName = " ";
        String validEmail       = "john.doe@example.com";
        String expectedMessage  = "Passenger name cannot be empty or blank.";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Passenger.create(invalidBlankName, validEmail),
                "Expected create() to throw an exception due to a blank name, but it did not"
        );

        assertEquals(
                expectedMessage,
                exception.getMessage(),
                "The exception message should exactly match our domain invariant rule"
        );
    }

    @Test
    @DisplayName("Given a null name, when create() is called, then it should throw an IllegalArgumentException")
    void givenNullName_whenCreate_thenThrowsException() {
        String validEmail       = "john.doe@example.com";
        String expectedMessage  = "Passenger name cannot be empty or blank.";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Passenger.create(null, validEmail),
                "Expected create() to throw an exception due to a null name, but it did not"
        );

        assertEquals(
                expectedMessage,
                exception.getMessage(),
                "The exception message should exactly match our domain invariant rule"
        );
    }

    @Test
    @DisplayName("Given a blank email, when create() is called, then it should throw an IllegalArgumentException")
    void givenBlankEmail_whenCreate_thenThrowsException() {
        String validName         = "John Doe";
        String invalidBlankEmail = " ";
        String expectedMessage   = "Passenger email cannot be empty or blank.";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Passenger.create(validName, invalidBlankEmail),
                "Expected create() to throw an exception due to a blank email, but it did not"
        );

        assertEquals(
                expectedMessage,
                exception.getMessage(),
                "The exception message should exactly match our domain invariant rule"
        );
    }

    @Test
    @DisplayName("Given a null email, when create() is called, then it should throw an IllegalArgumentException")
    void givenNullEmail_whenCreate_thenThrowsException() {
        String validName       = "John Doe";
        String expectedMessage = "Passenger email cannot be empty or blank.";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Passenger.create(validName, null),
                "Expected create() to throw an exception due to a null email, but it did not"
        );

        assertEquals(
                expectedMessage,
                exception.getMessage(),
                "The exception message should exactly match our domain invariant rule"
        );
    }

    @Test
    @DisplayName("Given valid persisted data, when fromPersistence() is called, then a restored passenger is instantiated")
    void givenPersistedData_whenFromPersistence_thenRestoredPassengerInstantiated() {
        Long existingId   = 467L;
        String validName  = "John Doe";
        String validEmail = "john.doe@example.com";

        Passenger restoredPassenger = Passenger.fromPersistence(existingId, validName, validEmail);

        assertAll(
                "Verify restored passenger state",
                () -> assertEquals(existingId, restoredPassenger.getId(), "The ID must match the database record"),
                () -> assertEquals(validName, restoredPassenger.getName(), "The name must match the database record"),
                () -> assertEquals(validEmail, restoredPassenger.getEmail(), "The email must match the database record")
        );
    }

    @Test
    @DisplayName("Given a null ID, when fromPersistence() is called, then it should throw an IllegalArgumentException")
    void givenNullId_whenFromPersistence_thenThrowsException() {
        String validName       = "John Doe";
        String validEmail      = "john.doe@example.com";
        String expectedMessage = "Passenger ID must be provided.";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Passenger.fromPersistence(null, validName, validEmail),
                "Expected fromPersistence() to throw an exception due to a null ID, but it did not"
        );

        assertEquals(
                expectedMessage,
                exception.getMessage(),
                "The exception message should exactly match our domain invariant rule"
        );
    }

    @Test
    @DisplayName("Given two passengers with the same ID, when compared, then they must be equal")
    void givenTwoPassengersWithSameId_thenShouldBeEqual() {
        Long existingId      = 467L;
        String oneName       = "John Doe";
        String oneEmail      = "john.doe@example.com";
        String anotherName   = "Jane Smith";
        String anotherEmail  = "jane.smith@example.com";

        Passenger passenger      = Passenger.fromPersistence(existingId, oneName, oneEmail);
        Passenger otherPassenger = Passenger.fromPersistence(existingId, anotherName, anotherEmail);

        assertEquals(passenger, otherPassenger, "Passengers with the same ID must be considered equal");
    }

    @Test
    @DisplayName("Given two passengers with different IDs, when compared, then they must not be equal")
    void givenTwoPassengersWithDistinctId_thenShouldNotBeEqual() {
        Long oneId           = 467L;
        Long anotherId       = 466L;
        String validName     = "John Doe";
        String validEmail    = "john.doe@example.com";

        Passenger passenger      = Passenger.fromPersistence(oneId, validName, validEmail);
        Passenger otherPassenger = Passenger.fromPersistence(anotherId, validName, validEmail);

        assertNotEquals(passenger, otherPassenger, "Passengers with different IDs must not be considered equal");
    }

    @Test
    @DisplayName("Given two passengers with the same ID, when computing hashCode, then they must return the same value")
    void givenTwoPassengersWithSameId_thenShouldHaveSameHashCode() {
        Long existingId      = 123L;
        String oneName       = "John Doe";
        String oneEmail      = "john.doe@example.com";
        String anotherName   = "Jane Smith";
        String anotherEmail  = "jane.smith@example.com";

        Passenger passenger      = Passenger.fromPersistence(existingId, oneName, oneEmail);
        Passenger otherPassenger = Passenger.fromPersistence(existingId, anotherName, anotherEmail);

        assertEquals(passenger.hashCode(), otherPassenger.hashCode(), "Passengers with the same ID must produce the same hash code");
    }
}