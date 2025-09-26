package com.stellarstay.hotelsystem.api.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.stellarstay.hotelsystem.api.validation.ReservationDates;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ReservationDates
public class CreateReservationRequest {
    @NotNull(message = "roomId is required.")
    private Long roomId;

    @NotBlank(message = "guestName is required.")
    private String guestName;

    @Min(value = 1, message = "guests must be at least 1.")
    private int guests;

    @NotNull(message = "checkInDate is required.")
    @FutureOrPresent(message = "checkInDate cannot be in the past.")
    private LocalDate checkInDate;

    @NotNull(message = "checkOutDate is required.")
    private LocalDate checkOutDate;

    private boolean breakfastIncluded;
}
