package com.stellarstay.hotelsystem.api.validation;

import com.stellarstay.hotelsystem.api.exception.BadRequestException;
import com.stellarstay.hotelsystem.domain.RoomType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import org.slf4j.MDC;

@Component
public class AvailableRoomsRequestValidator {
    public void validate(String type, LocalDate checkInDate, LocalDate checkOutDate, int guests) {
        if (type != null && RoomType.isInvalid(type)) {
            throw new BadRequestException(
                    "Invalid type value: '" + type + "'. Allowed values: "
                            + RoomType.validValues() + ".",
                    MDC.get("correlationId"));
        }
        if (guests <= 0) {
            throw new BadRequestException("Number of guests must be greater than zero.", MDC.get("correlationId"));
        }
        if (checkInDate == null || checkOutDate == null) {
            throw new BadRequestException("Check-in and check-out dates are required.", MDC.get("correlationId"));
        }
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new BadRequestException("Check-in date cannot be in the past.", MDC.get("correlationId"));
        }
        if (!checkOutDate.isAfter(checkInDate)) {
            throw new BadRequestException("Check-out date must be after check-in date.", MDC.get("correlationId"));
        }
    }
}
