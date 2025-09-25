package com.stellarstay.hotelsystem.ports.out;

import com.stellarstay.hotelsystem.domain.Room;
import com.stellarstay.hotelsystem.domain.RoomType;
import java.util.List;
import java.util.Optional;

public interface RoomPersistencePort {
    List<Room> findAvailableRooms(RoomType type, int capacity);
    Optional<Room> findById(Long id);
}

