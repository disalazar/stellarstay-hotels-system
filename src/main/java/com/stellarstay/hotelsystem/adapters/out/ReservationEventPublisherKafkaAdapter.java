package com.stellarstay.hotelsystem.adapters.out;

import com.stellarstay.hotelsystem.api.dto.ReservationCreatedEvent;
import com.stellarstay.hotelsystem.api.dto.ReservationMapper;
import com.stellarstay.hotelsystem.domain.Reservation;
import com.stellarstay.hotelsystem.ports.out.ReservationEventPublisherPort;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationEventPublisherKafkaAdapter implements ReservationEventPublisherPort {
    private final KafkaTemplate<String, ReservationCreatedEvent> kafkaTemplate;
    private final ReservationMapper reservationMapper;
    @Value("${spring.kafka.topic.reservations}")
    private String topic;
    private final CircuitBreaker reservationEventCircuitBreaker;
    private final Retry reservationEventRetry;

    @Override
    public void publishReservationCreated(Reservation reservation) {
        ReservationCreatedEvent event = reservationMapper.toEvent(reservation);
        Runnable publishTask = () -> kafkaTemplate.send(topic, event);
        Runnable decoratedWithCircuitBreaker = CircuitBreaker.decorateRunnable(reservationEventCircuitBreaker, publishTask);
        Runnable decoratedWithCircuitBreakerAndRetry = Retry.decorateRunnable(reservationEventRetry, decoratedWithCircuitBreaker);
        decoratedWithCircuitBreakerAndRetry.run();
    }
}
