package com.stellarstay.hotelsystem.api.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomNotAvailableExceptionTest {
    @Test
    void constructsWithMessageAndCorrelationId() {
        RoomNotAvailableException ex = new RoomNotAvailableException("msg", "corrId");
        assertEquals("msg", ex.getMessage());
        assertEquals("corrId", ex.getCorrelationId());
    }
}
