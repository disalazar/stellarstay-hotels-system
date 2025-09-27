package com.stellarstay.hotelsystem.api.dto;

import com.stellarstay.hotelsystem.domain.Reservation;
import com.stellarstay.hotelsystem.domain.Room;
import com.stellarstay.hotelsystem.domain.RoomType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationMapperTest {
    private final ReservationMapper mapper = new ReservationMapperImpl();

    @Test
    void mapsReservationToResponse() {
        Room room = new Room();
        room.setId(10L);
        room.setType(RoomType.JUNIOR_SUITE);
        Reservation reservation = new Reservation();
        reservation.setId(5L);
        reservation.setRoom(room);
        ReservationResponse response = mapper.toResponse(reservation);
        assertEquals(5L, response.getReservationId());
        assertEquals(10L, response.getRoomId());
        assertEquals(RoomType.JUNIOR_SUITE, response.getRoomType());
    }
}

