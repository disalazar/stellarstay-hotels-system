package com.stellarstay.hotelsystem.api.validation;

import com.stellarstay.hotelsystem.api.dto.CreateReservationRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservationDatesValidatorTest {
    private final ReservationDatesValidator validator = new ReservationDatesValidator();

    @Test
    void nullRequest_isValid() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void nullDates_isValid() {
        CreateReservationRequest req = new CreateReservationRequest();
        assertTrue(validator.isValid(req, null));
    }

    @Test
    void checkOutAfterCheckIn_isValid() {
        CreateReservationRequest req = new CreateReservationRequest();
        req.setCheckInDate(LocalDate.now());
        req.setCheckOutDate(LocalDate.now().plusDays(1));
        assertTrue(validator.isValid(req, null));
    }

    @Test
    void checkOutNotAfterCheckIn_isInvalid() {
        CreateReservationRequest req = new CreateReservationRequest();
        req.setCheckInDate(LocalDate.now());
        req.setCheckOutDate(LocalDate.now());
        assertFalse(validator.isValid(req, null));
    }
}

