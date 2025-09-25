package com.stellarstay.hotelsystem.api.dto;

import com.stellarstay.hotelsystem.domain.RoomType;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private Long reservationId;
    private Long roomId;
    private RoomType roomType;
    private String guestName;
    private int guests;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private boolean breakfastIncluded;
    private double totalPrice;
}