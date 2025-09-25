package com.stellarstay.hotelsystem.ports.in;


import com.stellarstay.hotelsystem.api.dto.RoomAvailabilityRequest;
import com.stellarstay.hotelsystem.api.dto.RoomResponse;

import java.util.List;

public interface RoomAvailabilityUseCase {
    List<RoomResponse> findAvailableRooms(RoomAvailabilityRequest request);
}
