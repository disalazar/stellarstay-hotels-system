package com.stellarstay.hotelsystem.adapters.in;

import com.stellarstay.hotelsystem.api.dto.CreateReservationRequest;
import com.stellarstay.hotelsystem.api.dto.ReservationMapper;
import com.stellarstay.hotelsystem.api.dto.ReservationResponse;
import com.stellarstay.hotelsystem.domain.*;
import com.stellarstay.hotelsystem.ports.in.ReservationUseCase;
import com.stellarstay.hotelsystem.ports.out.ReservationEventPublisherPort;
import com.stellarstay.hotelsystem.ports.out.ReservationPersistencePort;
import com.stellarstay.hotelsystem.ports.out.RoomPersistencePort;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationUseCaseImpl implements ReservationUseCase {
    private final RoomPersistencePort roomPersistencePort;
    private final ReservationPersistencePort reservationPersistencePort;
    private final PriceCalculator priceCalculator;
    private final ReservationEventPublisherPort eventPublisher;
    private final ReservationMapper reservationMapper;
    private final ReservationTransactionalHelper reservationTransactionalHelper;

    @Override
    @Bulkhead(name = "reservationBulkhead", type = Bulkhead.Type.THREADPOOL)
    public CompletableFuture<ReservationResponse> createReservation(CreateReservationRequest request) {
        return CompletableFuture.supplyAsync(() -> reservationTransactionalHelper.createReservationTransactional(request));
    }
}
