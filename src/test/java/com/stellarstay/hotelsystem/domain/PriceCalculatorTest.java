package com.stellarstay.hotelsystem.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PriceCalculatorTest {
    private final PriceCalculator calculator = new PriceCalculator();

    @Test
    void calculatesBaseRateForJuniorSuiteWithoutExtras() {
        double price = calculator.calculate(RoomType.JUNIOR_SUITE,
                LocalDate.of(2025, 9, 29), LocalDate.of(2025, 10, 1), 1, false);
        // 2 nights, weekdays, no breakfast, no discount
        assertEquals(120, price);
    }

    @Test
    void appliesWeekendSurcharge() {
        double price = calculator.calculate(RoomType.KING_SUITE,
                LocalDate.of(2025, 9, 27), LocalDate.of(2025, 9, 29), 1, false);
        // 27th is Saturday, 28th is Sunday, both weekend
        double expected = 90 * 1.25 + 90 * 1.25;
        assertEquals(expected, price);
    }

    @Test
    void appliesDurationDiscount() {
        double price = calculator.calculate(RoomType.PRESIDENTIAL_SUITE,
                LocalDate.of(2025, 9, 27), LocalDate.of(2025, 10, 7), 1, false);
        // 10 nights, discount 12 per night
        double base = 150;
        double expected = 0;
        for (int i = 0; i < 10; i++) {
            LocalDate day = LocalDate.of(2025, 9, 27).plusDays(i);
            double dayRate = base;
            if (day.getDayOfWeek().getValue() >= 6) dayRate *= 1.25;
            dayRate -= 12;
            expected += dayRate;
        }
        assertEquals(expected, price);
    }

    @Test
    void includesBreakfastCost() {
        double price = calculator.calculate(RoomType.JUNIOR_SUITE,
                LocalDate.of(2025, 9, 29), LocalDate.of(2025, 9, 30), 2, true);
        // 1 night, 2 guests, breakfast
        assertEquals(60 + 10, price);
    }

    @Test
    void neverReturnsNegativePrice() {
        double price = calculator.calculate(RoomType.JUNIOR_SUITE,
                LocalDate.of(2025, 9, 27), LocalDate.of(2025, 9, 28), 1, false);
        assertTrue(price >= 0);
    }
}
