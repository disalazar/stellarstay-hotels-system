package com.stellarstay.hotelsystem.ports.in;


import com.stellarstay.hotelsystem.api.dto.CreateReservationRequest;
import com.stellarstay.hotelsystem.api.dto.ReservationResponse;
import java.util.concurrent.CompletableFuture;

public interface ReservationUseCase {
    CompletableFuture<ReservationResponse> createReservation(CreateReservationRequest request);
}