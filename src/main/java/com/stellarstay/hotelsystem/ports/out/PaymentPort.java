package com.stellarstay.hotelsystem.ports.out;

public interface PaymentPort {
    void processPayment(String reservationId, double amount);
}
