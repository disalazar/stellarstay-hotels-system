package com.stellarstay.hotelsystem.adapters.in;

import com.stellarstay.hotelsystem.api.dto.CreateReservationRequest;
import com.stellarstay.hotelsystem.api.dto.ReservationMapper;
import com.stellarstay.hotelsystem.api.dto.ReservationResponse;
import com.stellarstay.hotelsystem.api.exception.BadRequestException;
import com.stellarstay.hotelsystem.api.exception.KafkaPublishException;
import com.stellarstay.hotelsystem.api.exception.RoomNotAvailableException;
import com.stellarstay.hotelsystem.domain.*;
import com.stellarstay.hotelsystem.ports.in.ReservationUseCase;
import com.stellarstay.hotelsystem.ports.out.ReservationEventPublisherPort;
import com.stellarstay.hotelsystem.ports.out.ReservationPersistencePort;
import com.stellarstay.hotelsystem.ports.out.RoomPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationUseCaseImpl implements ReservationUseCase {
    private final RoomPersistencePort roomPersistencePort;
    private final ReservationPersistencePort reservationPersistencePort;
    private final PriceCalculator priceCalculator;
    private final ReservationEventPublisherPort eventPublisher;
    private final ReservationMapper reservationMapper;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ReservationResponse createReservation(CreateReservationRequest request) {
        Room room = roomPersistencePort.findById(request.getRoomId())
                .orElseThrow(() -> new BadRequestException("Room with id '" + request.getRoomId() + "' not found"));
        List<Reservation> overlaps = reservationPersistencePort.findOverlappingReservations(room, request.getCheckInDate(), request.getCheckOutDate());
        if (!overlaps.isEmpty()) {
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
            throw new KafkaPublishException("Error publishing reservation event", e);
        }
        return reservationMapper.toResponse(saved);
    }
}
