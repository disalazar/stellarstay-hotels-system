package com.stellarstay.hotelsystem.ports.out;

public interface NotificationPort {
    void sendNotification(String reservationId, String message);
}
