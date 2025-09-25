package com.stellarstay.hotelsystem.adapters.in;


import com.stellarstay.hotelsystem.api.dto.CreateReservationRequest;
import com.stellarstay.hotelsystem.api.dto.ReservationMapper;
import com.stellarstay.hotelsystem.api.dto.ReservationResponse;
import com.stellarstay.hotelsystem.domain.*;
import com.stellarstay.hotelsystem.ports.in.ReservationUseCase;
import com.stellarstay.hotelsystem.ports.out.ReservationEventPublisherPort;
import com.stellarstay.hotelsystem.ports.out.ReservationPersistencePort;
import com.stellarstay.hotelsystem.ports.out.RoomPersistencePort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationUseCase {
    private final RoomPersistencePort roomPersistencePort;
    private final ReservationPersistencePort reservationPersistencePort;
    private final PriceCalculatorService priceCalculatorService;
    private final ReservationEventPublisherPort eventPublisher;
    private final ReservationMapper reservationMapper;

    @Override
    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request) {
        Room room = roomPersistencePort.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        List<Reservation> overlaps = reservationPersistencePort.findOverlappingReservations(room, request.getCheckInDate(), request.getCheckOutDate());
        if (!overlaps.isEmpty()) {
            throw new IllegalStateException("Room not available for the selected dates");
        }
        double totalPrice = priceCalculatorService.calculate(room.getType(), request.getCheckInDate(), request.getCheckOutDate(), request.getGuests(), request.isBreakfastIncluded());
        Reservation reservation = new Reservation();
        reservation.setRoom(room);
        reservation.setGuestName(request.getGuestName());
        reservation.setGuests(request.getGuests());
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setBreakfastIncluded(request.isBreakfastIncluded());
        reservation.setTotalPrice(totalPrice);
        Reservation saved = reservationPersistencePort.save(reservation);
        try {
            eventPublisher.publishReservationCreated(saved);
        } catch (Exception e) {
            System.err.println("Error publishing reservation event: " + e.getMessage());
        }
        return reservationMapper.toResponse(saved);
    }
}
