package com.stellarstay.hotelsystem.api.exception;

import lombok.Getter;

@Getter
public class RoomNotAvailableException extends RuntimeException {
    private final String correlationId;
    public RoomNotAvailableException(String message, String correlationId) {
        super(message);
        this.correlationId = correlationId;
    }
}
