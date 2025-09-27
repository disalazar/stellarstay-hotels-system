package com.stellarstay.hotelsystem.ports.out;

import com.stellarstay.hotelsystem.domain.Reservation;
import java.util.List;
import com.stellarstay.hotelsystem.domain.Room;
import java.time.LocalDate;

public interface ReservationPersistencePort {
    Reservation save(Reservation reservation);
    List<Reservation> findOverlappingReservationsWithLock(Room room, LocalDate checkIn, LocalDate checkOut);
}

