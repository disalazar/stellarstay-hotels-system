package com.stellarstay.hotelsystem.api;


import com.stellarstay.hotelsystem.api.dto.RoomAvailabilityRequest;
import com.stellarstay.hotelsystem.api.dto.RoomResponse;
import com.stellarstay.hotelsystem.ports.in.RoomAvailabilityUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rooms/available")
@RequiredArgsConstructor
public class AvailableRoomsController {
    private final RoomAvailabilityUseCase roomAvailabilityUseCase;

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAvailableRooms(
            @RequestParam com.stellarstay.hotelsystem.domain.RoomType type,
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
