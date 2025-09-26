package com.stellarstay.hotelsystem.domain;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum RoomType {
    JUNIOR_SUITE,
    KING_SUITE,
    PRESIDENTIAL_SUITE;

    public static boolean isInvalid(String value) {
        if (value == null) return true;
        try {
            RoomType.valueOf(value.toUpperCase());
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    public static String validValues() {
        return Arrays.stream(RoomType.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}
