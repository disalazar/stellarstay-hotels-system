package com.stellarstay.hotelsystem.api.validation;

import com.stellarstay.hotelsystem.api.exception.BadRequestException;
import com.stellarstay.hotelsystem.domain.RoomType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AvailableRoomsRequestValidator {
    public void validate(String type, LocalDate checkInDate, LocalDate checkOutDate, int guests) {
        if (type == null || type.isBlank()) {
            throw new BadRequestException("Room type is required.");
        }
        if (RoomType.isInvalid(type)) {
            throw new BadRequestException(
                    "Invalid type value: '" + type + "'. Allowed values: "
                            + RoomType.validValues() + ".");
        }
        if (guests <= 0) {
            throw new BadRequestException("Number of guests must be greater than zero.");
        }
        if (checkInDate == null || checkOutDate == null) {
            throw new BadRequestException("Check-in and check-out dates are required.");
        }
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new BadRequestException("Check-in date cannot be in the past.");
        }
        if (!checkOutDate.isAfter(checkInDate)) {
            throw new BadRequestException("Check-out date must be after check-in date.");
        }
    }
}
