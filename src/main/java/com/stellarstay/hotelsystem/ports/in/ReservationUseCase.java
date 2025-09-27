package com.stellarstay.hotelsystem.ports.in;


import com.stellarstay.hotelsystem.api.dto.CreateReservationRequest;
import com.stellarstay.hotelsystem.api.dto.ReservationResponse;

public interface ReservationUseCase {
    ReservationResponse createReservation(CreateReservationRequest request);
}