package com.stellarstay.hotelsystem.adapters.out.persistence;

import com.stellarstay.hotelsystem.domain.Reservation;
import com.stellarstay.hotelsystem.domain.Room;
import com.stellarstay.hotelsystem.domain.RoomType;
import com.stellarstay.hotelsystem.ports.out.ReservationPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private Room juniorSuiteRoom;

    @BeforeEach
    void setUp() {
        // Crear habitaciones para pruebas
        juniorSuiteRoom = new Room(null, RoomType.JUNIOR_SUITE, 2, true);
        Room kingSuiteRoom = new Room(null, RoomType.KING_SUITE, 3, true);
        Room presidentialSuiteRoom = new Room(null, RoomType.PRESIDENTIAL_SUITE, 4, true);

        // Guardar las habitaciones
        juniorSuiteRoom = roomRepository.save(juniorSuiteRoom);
        kingSuiteRoom = roomRepository.save(kingSuiteRoom);
        presidentialSuiteRoom = roomRepository.save(presidentialSuiteRoom);
    }

    @Test
    void findAvailableRooms_ShouldReturnAllAvailableRooms_WhenNoReservations() {
        // Given
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(5);

        // When
        List<Room> availableRooms = roomRepository.findAvailableRooms(
                null, 1, checkIn, checkOut);

        // Then
        assertEquals(3, availableRooms.size(), "Should return all available rooms");
        assertTrue(availableRooms.stream().anyMatch(room -> room.getType() == RoomType.JUNIOR_SUITE));
        assertTrue(availableRooms.stream().anyMatch(room -> room.getType() == RoomType.KING_SUITE));
        assertTrue(availableRooms.stream().anyMatch(room -> room.getType() == RoomType.PRESIDENTIAL_SUITE));
    }

    @Test
    void findAvailableRooms_ShouldFilterByType() {
        // Given
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(5);

        // When
        List<Room> availableSuites = roomRepository.findAvailableRooms(
                RoomType.KING_SUITE, 1, checkIn, checkOut);

        // Then
        assertEquals(1, availableSuites.size(), "Should return only KING_SUITE rooms");
        assertEquals(RoomType.KING_SUITE, availableSuites.get(0).getType());
    }

    @Test
    void findAvailableRooms_ShouldFilterByCapacity() {
        // Given
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(5);

        // When
        List<Room> availableRooms = roomRepository.findAvailableRooms(
                null, 4, checkIn, checkOut);

        // Then
        assertEquals(1, availableRooms.size(), "Should return only rooms with capacity >= 4");
        assertEquals(RoomType.PRESIDENTIAL_SUITE, availableRooms.get(0).getType());
    }

    @Test
    void findAvailableRooms_ShouldExcludeBookedRooms() {
        // Given
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(5);

        // Crear una reserva para la habitación junior suite
        Reservation reservation = Reservation.builder()
                .room(juniorSuiteRoom)
                .guestName("Test Guest")
                .guests(2)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .breakfastIncluded(false)
                .totalPrice(400.0)
                .build();

        ((ReservationPersistencePort) reservationRepository).save(reservation);

        // When
        List<Room> availableRooms = roomRepository.findAvailableRooms(
                null, 1, checkIn, checkOut);

        // Then
        assertEquals(2, availableRooms.size(), "Should exclude booked room");
        assertFalse(availableRooms.stream().anyMatch(room -> room.getType() == RoomType.JUNIOR_SUITE),
                "Junior Suite room should not be available due to reservation");
        assertTrue(availableRooms.stream().anyMatch(room -> room.getType() == RoomType.KING_SUITE));
        assertTrue(availableRooms.stream().anyMatch(room -> room.getType() == RoomType.PRESIDENTIAL_SUITE));
    }

    @Test
    void findAvailableRooms_ShouldIncludeRoomsWithNonOverlappingBookings() {
        // Given
        LocalDate checkIn = LocalDate.now().plusDays(10);
        LocalDate checkOut = LocalDate.now().plusDays(15);

        // Crear una reserva que no se solapa con las fechas de búsqueda
        Reservation reservation = Reservation.builder()
                .room(juniorSuiteRoom)
                .guestName("Test Guest")
                .guests(2)
                .checkInDate(LocalDate.now().plusDays(1))
                .checkOutDate(LocalDate.now().plusDays(5))
                .breakfastIncluded(false)
                .totalPrice(400.0)
                .build();

        ((ReservationPersistencePort) reservationRepository).save(reservation);

        // When
        List<Room> availableRooms = roomRepository.findAvailableRooms(
                null, 1, checkIn, checkOut);

        // Then
        assertEquals(3, availableRooms.size(), "Should include all rooms as there's no overlap");
    }
}
