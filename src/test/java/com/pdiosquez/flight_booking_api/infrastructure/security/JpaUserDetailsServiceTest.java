package com.pdiosquez.flight_booking_api.infrastructure.security;

import com.pdiosquez.flight_booking_api.domain.model.AppUser;
import com.pdiosquez.flight_booking_api.domain.model.Role;
import com.pdiosquez.flight_booking_api.domain.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaUserDetailsServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private JpaUserDetailsService jpaUserDetailsService;

    private AppUser appUser;
    private static final String TEST_EMAIL = "john.doe@example.com";
    private static final String HASHED_PASSWORD = "hashed_password_123";

    @BeforeEach
    void setUp() {
        appUser = AppUser.fromPersistence(
                1L,
                TEST_EMAIL,
                HASHED_PASSWORD,
                Role.USER
        );
    }

    @Test
    @DisplayName("Should return UserDetails when user exists with the given email")
    void shouldReturnUserDetailsWhenUserExistsWithGivenEmail() {
        // Arrange
        when(appUserRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.of(appUser));

        // Act
        UserDetails result = jpaUserDetailsService.loadUserByUsername(TEST_EMAIL);

        // Assert
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(TEST_EMAIL, result.getUsername()),
                () -> assertEquals(HASHED_PASSWORD, result.getPassword()),
                () -> assertTrue(
                        result.getAuthorities().stream()
                                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"))
                )
        );

        verify(appUserRepository).findByEmail(TEST_EMAIL);
        verifyNoMoreInteractions(appUserRepository);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist with the given email")
    void shouldThrowUsernameNotFoundExceptionWhenUserDoesNotExistWithGivenEmail() {
        // Arrange
        String unknownEmail = "john.doe123@example.com";
        when(appUserRepository.findByEmail(unknownEmail))
                .thenReturn(Optional.empty());

        // Act
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> jpaUserDetailsService.loadUserByUsername(unknownEmail)
        );

        // Assert
        assertEquals("User not found with email: " + unknownEmail, exception.getMessage());

        verify(appUserRepository).findByEmail(unknownEmail);
        verifyNoMoreInteractions(appUserRepository);
    }
}