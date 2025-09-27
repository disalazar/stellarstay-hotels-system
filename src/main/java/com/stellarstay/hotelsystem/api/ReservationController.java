package com.stellarstay.hotelsystem.api;


import com.stellarstay.hotelsystem.api.dto.CreateReservationRequest;
import com.stellarstay.hotelsystem.api.dto.ReservationResponse;
import com.stellarstay.hotelsystem.ports.in.ReservationUseCase;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/reservations")
@Slf4j
@Validated
public class ReservationController {
    private final ReservationUseCase reservationUseCase;
    private final MeterRegistry meterRegistry;
    private final ThreadPoolTaskExecutor reservationTaskExecutor;
    private Counter reservationCreatedCounter;

    public ReservationController(
        ReservationUseCase reservationUseCase,
        MeterRegistry meterRegistry,
        @Qualifier("reservationTaskExecutor") ThreadPoolTaskExecutor reservationTaskExecutor
    ) {
        this.reservationUseCase = reservationUseCase;
        this.meterRegistry = meterRegistry;
        this.reservationTaskExecutor = reservationTaskExecutor;
    }

    @PostConstruct
    public void init() {
        this.reservationCreatedCounter = meterRegistry.counter("reservations_created_total");
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<ReservationResponse>> createReservation(@Valid @RequestBody CreateReservationRequest request) {
        log.info("[ReservationController] POST /api/reservations - Request received: roomId={}, guestName={}, " +
                        "guests={}, checkInDate={}, checkOutDate={}, breakfastIncluded={}",
                request.getRoomId(), request.getGuestName(), request.getGuests(), request.getCheckInDate(),
                request.getCheckOutDate(), request.isBreakfastIncluded());
        return CompletableFuture.supplyAsync(
                () -> reservationUseCase.createReservation(request),
                reservationTaskExecutor
        ).thenApply(reservation -> {
            reservationCreatedCounter.increment();
            log.info("[ReservationController] POST /api/reservations - Reservation created with id={}",
                    reservation.getReservationId());
            return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
        });
    }
}
