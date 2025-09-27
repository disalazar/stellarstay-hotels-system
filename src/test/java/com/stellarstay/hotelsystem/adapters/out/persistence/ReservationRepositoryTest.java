package com.stellarstay.hotelsystem.adapters.out.persistence;

import com.stellarstay.hotelsystem.domain.Reservation;
import com.stellarstay.hotelsystem.domain.Room;
import com.stellarstay.hotelsystem.domain.RoomType;
import com.stellarstay.hotelsystem.ports.out.RoomPersistencePort;
import com.stellarstay.hotelsystem.ports.out.ReservationPersistencePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql"})
    void findOverlappingReservationsWithLock_ShouldReturnOverlappingReservations() {
        // Given
        Room room = ((RoomPersistencePort) roomRepository).findById(1L).orElseThrow();
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(5);

        Reservation reservation = Reservation.builder()
                .room(room)
                .guestName("Test Guest")
                .guests(2)
                .checkInDate(checkIn.plusDays(10)) // No overlapping
                .checkOutDate(checkOut.plusDays(15))
                .breakfastIncluded(false)
                .totalPrice(500.0)
                .build();

        ((ReservationPersistencePort) reservationRepository).save(reservation);

        // Crear una reserva que se superpone
        Reservation overlappingReservation = Reservation.builder()
                .room(room)
                .guestName("Overlapping Guest")
                .guests(2)
                .checkInDate(checkIn.plusDays(2)) // Overlapping
                .checkOutDate(checkOut.plusDays(2))
                .breakfastIncluded(true)
                .totalPrice(300.0)
                .build();

        ((ReservationPersistencePort) reservationRepository).save(overlappingReservation);

        // When
        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservationsWithLock(
                room, checkIn, checkOut);

        // Then
        assertTrue(overlappingReservations.stream()
                .anyMatch(res -> res.getId().equals(overlappingReservation.getId())),
                "Should find the overlapping reservation");

        assertTrue(overlappingReservations.stream()
                .noneMatch(res -> res.getId().equals(reservation.getId())),
                "Should not include non-overlapping reservation");
    }

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql"})
    void findOverlappingReservationsWithLock_WhenNoOverlap_ShouldReturnEmptyList() {
        // Given
        Room room = ((RoomPersistencePort) roomRepository).findById(1L).orElseThrow();
        LocalDate checkIn = LocalDate.now().plusDays(50);  // Dates far in the future
        LocalDate checkOut = LocalDate.now().plusDays(55); // No overlap expected

        // When
        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservationsWithLock(
                room, checkIn, checkOut);

        // Then
        assertTrue(overlappingReservations.isEmpty(),
                "Should not find any overlapping reservations for dates with no bookings");
    }

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql", "/clear-test-data.sql"})
    void saveReservation_ShouldPersistCorrectly() {
        // Given
        Room room = new Room(null, RoomType.JUNIOR_SUITE, 2, true);
        room = roomRepository.save(room);

        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = LocalDate.now().plusDays(8);

        Reservation reservation = Reservation.builder()
                .room(room)
                .guestName("New Guest")
                .guests(2)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .breakfastIncluded(true)
                .totalPrice(300.0)
                .build();

        // When
        Reservation savedReservation = ((ReservationPersistencePort) reservationRepository).save(reservation);

        // Then
        assertNotNull(savedReservation.getId(), "Saved reservation should have an ID");

        Reservation retrievedReservation = reservationRepository.findById(savedReservation.getId()).orElseThrow();
        assertEquals(reservation.getGuestName(), retrievedReservation.getGuestName());
        assertEquals(reservation.getCheckInDate(), retrievedReservation.getCheckInDate());
        assertEquals(reservation.getCheckOutDate(), retrievedReservation.getCheckOutDate());
        assertEquals(reservation.getTotalPrice(), retrievedReservation.getTotalPrice());
    }
}
