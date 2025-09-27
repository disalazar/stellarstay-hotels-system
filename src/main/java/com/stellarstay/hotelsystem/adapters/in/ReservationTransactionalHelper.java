package com.stellarstay.hotelsystem.adapters.in;

import com.stellarstay.hotelsystem.api.dto.CreateReservationRequest;
import com.stellarstay.hotelsystem.api.dto.ReservationMapper;
import com.stellarstay.hotelsystem.api.dto.ReservationResponse;
import com.stellarstay.hotelsystem.api.exception.BadRequestException;
import com.stellarstay.hotelsystem.api.exception.KafkaPublishException;
import com.stellarstay.hotelsystem.api.exception.RoomNotAvailableException;
import com.stellarstay.hotelsystem.domain.Reservation;
import com.stellarstay.hotelsystem.domain.Room;
import com.stellarstay.hotelsystem.ports.out.ReservationEventPublisherPort;
import com.stellarstay.hotelsystem.ports.out.ReservationPersistencePort;
import com.stellarstay.hotelsystem.ports.out.RoomPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationTransactionalHelper {
    private final RoomPersistencePort roomPersistencePort;
    private final ReservationPersistencePort reservationPersistencePort;
    private final ReservationEventPublisherPort eventPublisher;
    private final ReservationMapper reservationMapper;
    private final com.stellarstay.hotelsystem.domain.PriceCalculator priceCalculator;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ReservationResponse createReservationTransactional(CreateReservationRequest request) {
        Room room = roomPersistencePort.findById(request.getRoomId())
                .orElseThrow(() -> {
                    log.error("[ReservationTransactionalService] Room with id={} not found", request.getRoomId());
                    return new BadRequestException("Room with id '" + request.getRoomId() + "' not found");
                });
        List<Reservation> overlaps = reservationPersistencePort.findOverlappingReservationsWithLock(room, request.getCheckInDate(), request.getCheckOutDate());
        if (!overlaps.isEmpty()) {
            log.error("[ReservationTransactionalService] Room id={} not available for dates {} to {}", request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate());
            throw new RoomNotAvailableException("Room not available for the selected dates");
        }
        double totalPrice = priceCalculator.calculate(room.getType(), request.getCheckInDate(), request.getCheckOutDate(), request.getGuests(), request.isBreakfastIncluded());
        Reservation reservation = Reservation.builder()
            .room(room)
            .guestName(request.getGuestName())
            .guests(request.getGuests())
            .checkInDate(request.getCheckInDate())
            .checkOutDate(request.getCheckOutDate())
            .breakfastIncluded(request.isBreakfastIncluded())
            .totalPrice(totalPrice)
            .build();
        Reservation saved = reservationPersistencePort.save(reservation);
        try {
            eventPublisher.publishReservationCreated(saved);
        } catch (Exception e) {
            log.error("[ReservationTransactionalService] Failed to publish reservation event", e);
            throw new KafkaPublishException("Failed to publish reservation event", e);
        }
        return reservationMapper.toResponse(saved);
    }
}
