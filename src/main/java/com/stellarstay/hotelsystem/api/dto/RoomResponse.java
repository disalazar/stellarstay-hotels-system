package com.stellarstay.hotelsystem.api.dto;

import com.stellarstay.hotelsystem.domain.RoomType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private Long roomId;
    private RoomType type;
    private int capacity;
    private boolean available;
}
