package com.stellarstay.hotelsystem.api.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BadRequestExceptionTest {
    @Test
    void constructsWithMessageAndCorrelationId() {
        BadRequestException ex = new BadRequestException("msg", "corrId");
        assertEquals("msg", ex.getMessage());
        assertEquals("corrId", ex.getCorrelationId());
    }
}

