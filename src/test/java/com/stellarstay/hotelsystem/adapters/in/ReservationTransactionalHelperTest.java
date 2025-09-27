package com.stellarstay.hotelsystem.adapters.in;

import com.stellarstay.hotelsystem.api.dto.CreateReservationRequest;
import com.stellarstay.hotelsystem.api.dto.ReservationMapper;
import com.stellarstay.hotelsystem.api.dto.ReservationResponse;
import com.stellarstay.hotelsystem.api.exception.BadRequestException;
import com.stellarstay.hotelsystem.api.exception.KafkaPublishException;
import com.stellarstay.hotelsystem.api.exception.RoomNotAvailableException;
import com.stellarstay.hotelsystem.domain.PriceCalculator;
import com.stellarstay.hotelsystem.domain.Reservation;
import com.stellarstay.hotelsystem.domain.Room;
import com.stellarstay.hotelsystem.domain.RoomType;
import com.stellarstay.hotelsystem.ports.out.ReservationEventPublisherPort;
import com.stellarstay.hotelsystem.ports.out.ReservationPersistencePort;
import com.stellarstay.hotelsystem.ports.out.RoomPersistencePort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationTransactionalHelperTest {

    @Mock
    private RoomPersistencePort roomPersistencePort;

    @Mock
    private ReservationPersistencePort reservationPersistencePort;

    @Mock
    private ReservationEventPublisherPort eventPublisher;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private PriceCalculator priceCalculator;

    @InjectMocks
    private ReservationTransactionalHelper reservationTransactionalHelper;

    private CreateReservationRequest validRequest;
    private Room room;
    private Reservation savedReservation;
    private ReservationResponse expectedResponse;

    @BeforeEach
    void setUp() {
        // Configurar MDC para los tests
        MDC.put("correlationId", "test-correlation-id");

        // Configurar datos de prueba
        room = new Room(123L, RoomType.JUNIOR_SUITE, 2, true);

        validRequest = new CreateReservationRequest();
        validRequest.setRoomId(123L);
        validRequest.setGuestName("John Doe");
        validRequest.setGuests(2);
        validRequest.setCheckInDate(LocalDate.now().plusDays(1));
        validRequest.setCheckOutDate(LocalDate.now().plusDays(3));
        validRequest.setBreakfastIncluded(true);

        savedReservation = Reservation.builder()
                .id(456L)
                .room(room)
                .guestName("John Doe")
                .guests(2)
                .checkInDate(LocalDate.now().plusDays(1))
                .checkOutDate(LocalDate.now().plusDays(3))
                .breakfastIncluded(true)
                .totalPrice(250.0)
                .build();

        expectedResponse = new ReservationResponse();
        expectedResponse.setReservationId(456L);
    }

    @Test
    void createReservationTransactional_ShouldCreateReservationSuccessfully() {
        // Given
        when(roomPersistencePort.findById(123L)).thenReturn(Optional.of(room));
        when(reservationPersistencePort.findOverlappingReservationsWithLock(
                any(Room.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>());
        when(priceCalculator.calculate(
                any(RoomType.class), any(LocalDate.class), any(LocalDate.class),
                any(Integer.class), any(Boolean.class)))
                .thenReturn(250.0);
        when(reservationPersistencePort.save(any(Reservation.class))).thenReturn(savedReservation);
        when(reservationMapper.toResponse(savedReservation)).thenReturn(expectedResponse);

        // When
        ReservationResponse actualResponse = reservationTransactionalHelper.createReservationTransactional(validRequest);

        // Then
        assertEquals(expectedResponse, actualResponse);
        verify(roomPersistencePort).findById(123L);
        verify(reservationPersistencePort).findOverlappingReservationsWithLock(
                room, validRequest.getCheckInDate(), validRequest.getCheckOutDate());
        verify(priceCalculator).calculate(
                room.getType(), validRequest.getCheckInDate(), validRequest.getCheckOutDate(),
                validRequest.getGuests(), validRequest.isBreakfastIncluded());
        verify(reservationPersistencePort).save(any(Reservation.class));
        verify(eventPublisher).publishReservationCreated(savedReservation);
        verify(reservationMapper).toResponse(savedReservation);
    }

    @Test
    void createReservationTransactional_WhenRoomNotFound_ShouldThrowBadRequestException() {
        // Given
        when(roomPersistencePort.findById(123L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BadRequestException.class, () ->
                reservationTransactionalHelper.createReservationTransactional(validRequest));

        verify(reservationPersistencePort, never()).findOverlappingReservationsWithLock(
                any(Room.class), any(LocalDate.class), any(LocalDate.class));
        verify(reservationPersistencePort, never()).save(any(Reservation.class));
        verify(eventPublisher, never()).publishReservationCreated(any(Reservation.class));
    }

    @Test
    void createReservationTransactional_WhenRoomNotAvailable_ShouldThrowRoomNotAvailableException() {
        // Given
        when(roomPersistencePort.findById(123L)).thenReturn(Optional.of(room));

        List<Reservation> overlappingReservations = List.of(
                Reservation.builder().id(789L).room(room).build()
        );

        when(reservationPersistencePort.findOverlappingReservationsWithLock(
                any(Room.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(overlappingReservations);

        // When & Then
        assertThrows(RoomNotAvailableException.class, () ->
                reservationTransactionalHelper.createReservationTransactional(validRequest));

        verify(reservationPersistencePort, never()).save(any(Reservation.class));
        verify(eventPublisher, never()).publishReservationCreated(any(Reservation.class));
    }

    @Test
    void createReservationTransactional_WhenEventPublishFails_ShouldThrowKafkaPublishException() {
        // Given
        when(roomPersistencePort.findById(123L)).thenReturn(Optional.of(room));
        when(reservationPersistencePort.findOverlappingReservationsWithLock(
                any(Room.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>());
        when(priceCalculator.calculate(
                any(RoomType.class), any(LocalDate.class), any(LocalDate.class),
                any(Integer.class), any(Boolean.class)))
                .thenReturn(250.0);
        when(reservationPersistencePort.save(any(Reservation.class))).thenReturn(savedReservation);

        doThrow(new RuntimeException("Kafka connection error"))
                .when(eventPublisher).publishReservationCreated(any(Reservation.class));

        // When & Then
        assertThrows(KafkaPublishException.class, () ->
                reservationTransactionalHelper.createReservationTransactional(validRequest));

        verify(reservationPersistencePort).save(any(Reservation.class));
        verify(eventPublisher).publishReservationCreated(savedReservation);
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }
}
