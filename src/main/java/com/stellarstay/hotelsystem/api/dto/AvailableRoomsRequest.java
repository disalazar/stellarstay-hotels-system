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
public class AvailableRoomsRequest {
    private RoomType type;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int guests;
}
