package com.stellarstay.hotelsystem.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomTypeTest {
    @Test
    void isInvalidDetectsInvalidAndValidTypes() {
        assertTrue(RoomType.isInvalid("INVALID"));
        assertFalse(RoomType.isInvalid("JUNIOR_SUITE"));
        assertFalse(RoomType.isInvalid("king_suite"));
    }

    @Test
    void validValuesReturnsAllEnumNames() {
        String values = RoomType.validValues();
        assertTrue(values.contains("JUNIOR_SUITE"));
        assertTrue(values.contains("KING_SUITE"));
        assertTrue(values.contains("PRESIDENTIAL_SUITE"));
    }
}

