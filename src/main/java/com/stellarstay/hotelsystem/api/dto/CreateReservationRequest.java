package com.stellarstay.hotelsystem.api.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequest {
    private Long roomId;
    private String guestName;
    private int guests;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private boolean breakfastIncluded;
}
