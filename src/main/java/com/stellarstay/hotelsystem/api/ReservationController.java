package com.stellarstay.hotelsystem.api;


import com.stellarstay.hotelsystem.api.dto.CreateReservationRequest;
import com.stellarstay.hotelsystem.api.dto.ReservationResponse;
import com.stellarstay.hotelsystem.api.dto.RoomAvailabilityRequest;
import com.stellarstay.hotelsystem.api.dto.RoomResponse;
import com.stellarstay.hotelsystem.domain.RoomType;
import com.stellarstay.hotelsystem.ports.in.ReservationUseCase;
import com.stellarstay.hotelsystem.ports.in.RoomAvailabilityUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationUseCase reservationUseCase;
    private final RoomAvailabilityUseCase roomAvailabilityUseCase;

    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody CreateReservationRequest request) {
        try {
            ReservationResponse reservation = reservationUseCase.createReservation(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }

    @GetMapping("/available-rooms")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms(
            @RequestParam RoomType type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam int guests
    ) {
        RoomAvailabilityRequest request = new RoomAvailabilityRequest();
        request.setType(type);
        request.setCheckInDate(checkInDate);
        request.setCheckOutDate(checkOutDate);
        request.setGuests(guests);
        List<RoomResponse> availableRooms = roomAvailabilityUseCase.findAvailableRooms(request);
        return ResponseEntity.ok(availableRooms);
    }
}
