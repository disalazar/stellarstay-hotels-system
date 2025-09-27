package com.stellarstay.hotelsystem.adapters.out.persistence;

import com.stellarstay.hotelsystem.domain.Reservation;
import com.stellarstay.hotelsystem.domain.Room;
import com.stellarstay.hotelsystem.ports.out.ReservationPersistencePort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationPersistencePort {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reservation r WHERE r.room = :room AND r.checkOutDate > :checkIn AND r.checkInDate < :checkOut")
    List<Reservation> findOverlappingReservationsWithLock(@Param("room") Room room, @Param("checkIn") LocalDate checkIn, @Param("checkOut") LocalDate checkOut);
}
