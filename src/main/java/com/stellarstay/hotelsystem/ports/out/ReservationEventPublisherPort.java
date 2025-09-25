package com.stellarstay.hotelsystem.ports.out;

import com.stellarstay.hotelsystem.domain.Reservation;

public interface ReservationEventPublisherPort {
    void publishReservationCreated(Reservation reservation);
}

