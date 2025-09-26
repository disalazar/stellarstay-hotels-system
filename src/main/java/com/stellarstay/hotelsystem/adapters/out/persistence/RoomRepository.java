package com.stellarstay.hotelsystem.adapters.out.persistence;

import com.stellarstay.hotelsystem.domain.Room;
import com.stellarstay.hotelsystem.domain.RoomType;
import com.stellarstay.hotelsystem.ports.out.RoomPersistencePort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, RoomPersistencePort {
    @Override
    @Query("SELECT r FROM Room r WHERE (:type IS NULL OR r.type = :type) AND r.capacity >= :capacity AND r.available = true")
    List<Room> findAvailableRooms(@Param("type") RoomType type, @Param("capacity") int capacity);

    @Override
    Optional<Room> findById(Long id);
}
