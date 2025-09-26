package com.stellarstay.hotelsystem.ports.out;

import com.stellarstay.hotelsystem.domain.Room;
import com.stellarstay.hotelsystem.domain.RoomType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomPersistencePort {
    List<Room> findAvailableRooms(RoomType type, int capacity, LocalDate checkIn, LocalDate checkOut);
    Optional<Room> findById(Long id);
}
