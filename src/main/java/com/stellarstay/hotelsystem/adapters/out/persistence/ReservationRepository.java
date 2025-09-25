package com.stellarstay.hotelsystem.adapters.out.persistence;

import com.stellarstay.hotelsystem.domain.Reservation;
import com.stellarstay.hotelsystem.domain.Room;
import com.stellarstay.hotelsystem.ports.out.ReservationPersistencePort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationPersistencePort {
    @Override
    @Query("SELECT r FROM Reservation r WHERE r.room = :room AND r.checkOutDate > :checkIn AND r.checkInDate < :checkOut")
    List<Reservation> findOverlappingReservations(@Param("room") Room room, @Param("checkIn") LocalDate checkIn, @Param("checkOut") LocalDate checkOut);

    @Override
    Optional<Reservation> findById(Long id);
}
