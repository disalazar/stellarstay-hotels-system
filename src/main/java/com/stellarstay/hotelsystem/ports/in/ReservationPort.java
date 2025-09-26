package com.stellarstay.hotelsystem.ports.in;

import com.stellarstay.hotelsystem.domain.Reservation;

import java.time.LocalDate;

public interface ReservationPort {
    Reservation createReservation(Long roomId, String guestName, int guests, LocalDate checkIn, LocalDate checkOut, boolean breakfastIncluded);
}
