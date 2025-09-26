package com.stellarstay.hotelsystem.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.stellarstay.hotelsystem.api.dto.CreateReservationRequest;

public class ReservationDatesValidator implements ConstraintValidator<ReservationDates, CreateReservationRequest> {
    @Override
    public boolean isValid(CreateReservationRequest value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (value.getCheckInDate() == null || value.getCheckOutDate() == null) return true;
        return value.getCheckOutDate().isAfter(value.getCheckInDate());
    }
}

