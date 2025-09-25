package com.stellarstay.hotelsystem.adapters.out;

import com.stellarstay.hotelsystem.api.dto.ReservationCreatedEvent;
import com.stellarstay.hotelsystem.api.dto.ReservationMapper;
import com.stellarstay.hotelsystem.domain.Reservation;
import com.stellarstay.hotelsystem.ports.out.ReservationEventPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaReservationEventPublisher implements ReservationEventPublisherPort {
    private final KafkaTemplate<String, ReservationCreatedEvent> kafkaTemplate;
    private final ReservationMapper reservationMapper;
    @Value("${kafka.topic.reservations:reservations}")
    private String topic;

    @Override
    public void publishReservationCreated(Reservation reservation) {
        ReservationCreatedEvent event = reservationMapper.toEvent(reservation);
        kafkaTemplate.send(topic, event);
    }
}
