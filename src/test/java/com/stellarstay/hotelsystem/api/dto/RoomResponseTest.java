package com.stellarstay.hotelsystem.api.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomResponseTest {
    @Test
    void builderAndGettersWork() {
        RoomResponse response = RoomResponse.builder()
                .roomId(5L)
                .type("JUNIOR_SUITE")
                .capacity(2)
                .available(true)
                .build();
        assertEquals(5L, response.getRoomId());
        assertEquals("JUNIOR_SUITE", response.getType());
        assertEquals(2, response.getCapacity());
        assertTrue(response.isAvailable());
    }
}

