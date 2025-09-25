package com.stellarstay.hotelsystem.ports.out;

import com.stellarstay.hotelsystem.domain.Reservation;
import java.util.Optional;
import java.util.List;
import com.stellarstay.hotelsystem.domain.Room;
import java.time.LocalDate;

public interface ReservationPersistencePort {
    Reservation save(Reservation reservation);
    Optional<Reservation> findById(Long id);
    List<Reservation> findOverlappingReservations(Room room, LocalDate checkIn, LocalDate checkOut);
}

