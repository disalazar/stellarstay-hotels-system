package com.stellarstay.hotelsystem.api.validation;

import com.stellarstay.hotelsystem.api.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AvailableRoomsRequestValidatorTest {
    private final AvailableRoomsRequestValidator validator = new AvailableRoomsRequestValidator();

    @Test
    void validRequest_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validate("JUNIOR_SUITE", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), 1));
    }

    @Test
    void invalidType_throwsException() {
        Exception ex = assertThrows(BadRequestException.class, () -> validator.validate("INVALID", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), 1));
        assertTrue(ex.getMessage().contains("Invalid type value"));
    }

    @Test
    void guestsZeroOrNegative_throwsException() {
        Exception ex = assertThrows(BadRequestException.class, () -> validator.validate("JUNIOR_SUITE", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), 0));
        assertTrue(ex.getMessage().contains("Number of guests must be greater than zero"));
    }

    @Test
    void nullDates_throwsException() {
        Exception ex = assertThrows(BadRequestException.class, () -> validator.validate("JUNIOR_SUITE", null, null, 1));
        assertTrue(ex.getMessage().contains("Check-in and check-out dates are required"));
    }

    @Test
    void checkInInPast_throwsException() {
        Exception ex = assertThrows(BadRequestException.class, () -> validator.validate("JUNIOR_SUITE", LocalDate.now().minusDays(1), LocalDate.now().plusDays(2), 1));
        assertTrue(ex.getMessage().contains("Check-in date cannot be in the past"));
    }

    @Test
    void checkOutNotAfterCheckIn_throwsException() {
        Exception ex = assertThrows(BadRequestException.class, () -> validator.validate("JUNIOR_SUITE", LocalDate.now().plusDays(1), LocalDate.now().plusDays(1), 1));
        assertTrue(ex.getMessage().contains("Check-out date must be after check-in date"));
    }
}
