package com.stellarstay.hotelsystem.ports.in;

import com.stellarstay.hotelsystem.domain.Room;
import com.stellarstay.hotelsystem.domain.RoomType;

import java.time.LocalDate;
import java.util.List;

public interface RoomAvailabilityPort {
    List<Room> findAvailableRooms(RoomType type, LocalDate checkIn, LocalDate checkOut, int guests);
}
