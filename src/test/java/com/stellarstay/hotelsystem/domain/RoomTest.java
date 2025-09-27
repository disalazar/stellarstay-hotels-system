package com.stellarstay.hotelsystem.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {
    @Test
    void constructorAndGettersWork() {
        Room room = new Room(2L, RoomType.KING_SUITE, 4, false);
        assertEquals(2L, room.getId());
        assertEquals(RoomType.KING_SUITE, room.getType());
        assertEquals(4, room.getCapacity());
        assertFalse(room.isAvailable());
    }
}

