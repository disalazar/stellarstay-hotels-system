package com.stellarstay.hotelsystem.api;


import com.stellarstay.hotelsystem.api.dto.CreateReservationRequest;
import com.stellarstay.hotelsystem.api.dto.ReservationResponse;
import com.stellarstay.hotelsystem.ports.in.ReservationUseCase;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ReservationController {
    private final ReservationUseCase reservationUseCase;
    private final MeterRegistry meterRegistry;
    private Counter reservationCreatedCounter;

    @PostConstruct
    public void init() {
        this.reservationCreatedCounter = meterRegistry.counter("reservations_created_total");
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody CreateReservationRequest request) {
        ReservationResponse reservation = reservationUseCase.createReservation(request);
        reservationCreatedCounter.increment();
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }
}
