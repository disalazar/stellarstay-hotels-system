package com.stellarstay.hotelsystem.adapters.in;

import com.stellarstay.hotelsystem.api.dto.CreateReservationRequest;
import com.stellarstay.hotelsystem.api.dto.ReservationResponse;
import com.stellarstay.hotelsystem.api.dto.ReservationMapper;
import com.stellarstay.hotelsystem.domain.PriceCalculator;
import com.stellarstay.hotelsystem.ports.out.ReservationEventPublisherPort;
import com.stellarstay.hotelsystem.ports.out.ReservationPersistencePort;
import com.stellarstay.hotelsystem.ports.out.RoomPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationUseCaseImplTest {
    private RoomPersistencePort roomPersistencePort;
    private ReservationPersistencePort reservationPersistencePort;
    private PriceCalculator priceCalculator;
    private ReservationMapper reservationMapper;
    private ReservationEventPublisherPort eventPublisher;
    private ReservationTransactionalHelper reservationTransactionalHelper;
    private ReservationUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        roomPersistencePort = mock(RoomPersistencePort.class);
        reservationPersistencePort = mock(ReservationPersistencePort.class);
        priceCalculator = mock(PriceCalculator.class);
        reservationMapper = mock(ReservationMapper.class);
        eventPublisher = mock(ReservationEventPublisherPort.class);
        reservationTransactionalHelper = mock(ReservationTransactionalHelper.class);
        useCase = new ReservationUseCaseImpl(roomPersistencePort, reservationPersistencePort, priceCalculator, reservationMapper, eventPublisher, reservationTransactionalHelper);
    }

    @Test
    void createReservation_DelegatesToTransactionalHelper() {
        CreateReservationRequest req = new CreateReservationRequest();
        ReservationResponse resp = new ReservationResponse();
        when(reservationTransactionalHelper.createReservationTransactional(req)).thenReturn(resp);
        ReservationResponse result = useCase.createReservation(req);
        assertEquals(resp, result);
        verify(reservationTransactionalHelper).createReservationTransactional(req);
    }
}
