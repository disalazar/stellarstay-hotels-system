package com.stellarstay.hotelsystem.adapters.out;

import com.stellarstay.hotelsystem.ports.out.PaymentPort;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DummyPaymentAdapter implements PaymentPort {
    private final CircuitBreaker paymentCircuitBreaker;
    private final Retry paymentRetry;

    @Override
    public void processPayment(String reservationId, double amount) {
        Runnable paymentTask = () -> System.out.println("Processing dummy payment for reservation: " + reservationId + ", amount: " + amount);
        Runnable decoratedWithCircuitBreaker = CircuitBreaker.decorateRunnable(paymentCircuitBreaker, paymentTask);
        Runnable decoratedWithCircuitBreakerAndRetry = Retry.decorateRunnable(paymentRetry, decoratedWithCircuitBreaker);
        decoratedWithCircuitBreakerAndRetry.run();
    }
}