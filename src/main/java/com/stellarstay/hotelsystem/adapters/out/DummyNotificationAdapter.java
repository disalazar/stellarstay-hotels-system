package com.stellarstay.hotelsystem.adapters.out;

import com.stellarstay.hotelsystem.ports.out.NotificationPort;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DummyNotificationAdapter implements NotificationPort {
    private final CircuitBreaker notificationCircuitBreaker;
    private final Retry notificationRetry;

    @Override
    public void sendNotification(String reservationId, String message) {
        Runnable notificationTask = () -> System.out.println("Sending dummy notification for reservation: " + reservationId + ", message: " + message);
        Runnable decoratedWithCircuitBreaker = CircuitBreaker.decorateRunnable(notificationCircuitBreaker, notificationTask);
        Runnable decoratedWithCircuitBreakerAndRetry = Retry.decorateRunnable(notificationRetry, decoratedWithCircuitBreaker);
        decoratedWithCircuitBreakerAndRetry.run();
    }
}
