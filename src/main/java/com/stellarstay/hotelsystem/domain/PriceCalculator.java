package com.stellarstay.hotelsystem.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class PriceCalculator {
    public double calculate(RoomType roomType, LocalDate checkIn, LocalDate checkOut, int guests, boolean breakfastIncluded) {
        double baseRate = getBaseRate(roomType);
        int days = (int) (checkOut.toEpochDay() - checkIn.toEpochDay());
        double total = 0;
        for (int i = 0; i < days; i++) {
            LocalDate day = checkIn.plusDays(i);
            double dayRate = baseRate;
            if (isWeekend(day)) {
                dayRate *= 1.25;
            }
            dayRate -= getDurationDiscount(days);
            if (breakfastIncluded) {
                dayRate += 5 * guests;
            }
            total += dayRate;
        }
        return Math.max(total, 0);
    }

    private double getBaseRate(RoomType type) {
        return switch (type) {
            case SUITE_JUNIOR -> 60;
            case SUITE_KING -> 90;
            case SUITE_PRESIDENCIAL -> 150;
        };
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    private double getDurationDiscount(int days) {
        if (days >= 10) return 12;
        if (days >= 7) return 8;
        if (days >= 4) return 4;
        return 0;
    }
}

