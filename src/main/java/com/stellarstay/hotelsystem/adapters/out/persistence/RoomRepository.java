package com.stellarstay.hotelsystem.adapters.out.persistence;

import com.stellarstay.hotelsystem.domain.Room;
import com.stellarstay.hotelsystem.domain.RoomType;
import com.stellarstay.hotelsystem.ports.out.RoomPersistencePort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, RoomPersistencePort {
    @Override
    @Query("""
        SELECT r FROM Room r
        WHERE r.available = true
          AND r.capacity >= :capacity
          AND (:type IS NULL OR r.type = :type)
          AND NOT EXISTS (
            SELECT 1 FROM Reservation res
            WHERE res.room = r
              AND res.checkOutDate > :checkIn
              AND res.checkInDate < :checkOut
          )
        """)
    List<Room> findAvailableRooms(
        @Param("type") RoomType type,
        @Param("capacity") int capacity,
        @Param("checkIn") java.time.LocalDate checkIn,
        @Param("checkOut") java.time.LocalDate checkOut
    );
}
