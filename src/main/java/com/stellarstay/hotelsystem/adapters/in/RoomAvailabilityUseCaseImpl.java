package com.stellarstay.hotelsystem.adapters.in;

import com.stellarstay.hotelsystem.api.dto.RoomAvailabilityRequest;
import com.stellarstay.hotelsystem.api.dto.RoomResponse;
import com.stellarstay.hotelsystem.domain.Room;
import com.stellarstay.hotelsystem.ports.in.RoomAvailabilityUseCase;
import com.stellarstay.hotelsystem.ports.out.RoomPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomAvailabilityUseCaseImpl implements RoomAvailabilityUseCase {
    private final RoomPersistencePort roomPersistencePort;

    @Override
    public List<RoomResponse> findAvailableRooms(RoomAvailabilityRequest request) {
        List<Room> availableRooms = roomPersistencePort.findAvailableRooms(
            request.getType(),
            request.getGuests(),
            request.getCheckInDate(),
            request.getCheckOutDate()
        );
        return availableRooms.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private RoomResponse mapToResponse(Room room) {
        return RoomResponse.builder()
            .roomId(room.getId())
            .type(room.getType().name())
            .capacity(room.getCapacity())
            .available(room.isAvailable())
            .build();
    }
}
