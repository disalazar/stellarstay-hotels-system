package com.stellarstay.hotelsystem.api.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    private final String correlationId;

    public BadRequestException(String message, String correlationId) {
        super(message);
        this.correlationId = correlationId;
    }
}
