package com.stellarstay.hotelsystem.api;

import com.stellarstay.hotelsystem.api.dto.RoomAvailabilityRequest;
import com.stellarstay.hotelsystem.api.dto.RoomResponse;
import com.stellarstay.hotelsystem.api.validation.AvailableRoomsRequestValidator;
import com.stellarstay.hotelsystem.ports.in.RoomAvailabilityUseCase;
import com.stellarstay.hotelsystem.domain.RoomType;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomAvailabilityUseCase roomAvailabilityUseCase;
    private final AvailableRoomsRequestValidator validator;
    private final MeterRegistry meterRegistry;
    private Counter availableRoomsQueriedCounter;

    @PostConstruct
    public void init() {
        this.availableRoomsQueriedCounter = meterRegistry.counter("available_rooms_queried_total");
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms(
            @RequestParam(required = false) String type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam int guests
    ) {
        availableRoomsQueriedCounter.increment();
        validator.validate(type, checkInDate, checkOutDate, guests);
        RoomType roomType = (type != null && !type.isBlank()) ? RoomType.valueOf(type) : null;
        RoomAvailabilityRequest request = new RoomAvailabilityRequest(roomType,
                checkInDate, checkOutDate, guests);
        List<RoomResponse> availableRooms = roomAvailabilityUseCase.findAvailableRooms(request);
        return ResponseEntity.ok(availableRooms);
    }
}
