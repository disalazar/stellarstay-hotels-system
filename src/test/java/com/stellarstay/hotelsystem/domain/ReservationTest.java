package com.stellarstay.hotelsystem.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {
    @Test
    void builderAndGettersWork() {
        Room room = new Room(1L, RoomType.JUNIOR_SUITE, 2, true);
        Reservation reservation = Reservation.builder()
                .id(10L)
                .room(room)
                .guestName("John Doe")
                .guests(2)
                .checkInDate(LocalDate.of(2025, 10, 1))
                .checkOutDate(LocalDate.of(2025, 10, 5))
                .breakfastIncluded(true)
                .totalPrice(300.0)
                .build();
        assertEquals(10L, reservation.getId());
        assertEquals(room, reservation.getRoom());
        assertEquals("John Doe", reservation.getGuestName());
        assertEquals(2, reservation.getGuests());
        assertEquals(LocalDate.of(2025, 10, 1), reservation.getCheckInDate());
        assertEquals(LocalDate.of(2025, 10, 5), reservation.getCheckOutDate());
        assertTrue(reservation.isBreakfastIncluded());
        assertEquals(300.0, reservation.getTotalPrice());
    }
}