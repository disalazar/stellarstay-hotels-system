package com.stellarstay.hotelsystem.api.dto;

import com.stellarstay.hotelsystem.domain.RoomType;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservationCreatedEventTest {
    @Test
    void constructorAndGettersWork() {
        ReservationCreatedEvent event = new ReservationCreatedEvent(1L, 2L, RoomType.KING_SUITE, "Jane", 3,
                LocalDate.of(2025, 10, 1), LocalDate.of(2025, 10, 5), true, 500.0);
        assertEquals(1L, event.getReservationId());
        assertEquals(2L, event.getRoomId());
        assertEquals(RoomType.KING_SUITE, event.getRoomType());
        assertEquals("Jane", event.getGuestName());
        assertEquals(3, event.getGuests());
        assertEquals(LocalDate.of(2025, 10, 1), event.getCheckInDate());
        assertEquals(LocalDate.of(2025, 10, 5), event.getCheckOutDate());
        assertTrue(event.isBreakfastIncluded());
        assertEquals(500.0, event.getTotalPrice());
    }
}

