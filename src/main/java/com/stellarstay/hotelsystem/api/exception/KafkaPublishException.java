package com.stellarstay.hotelsystem.api.exception;

import lombok.Getter;

@Getter
public class KafkaPublishException extends RuntimeException {
    private final String correlationId;
    public KafkaPublishException(String message, Throwable cause, String correlationId) {
        super(message, cause);
        this.correlationId = correlationId;
    }
}
