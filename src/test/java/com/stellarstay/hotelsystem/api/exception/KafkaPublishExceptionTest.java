package com.stellarstay.hotelsystem.api.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class KafkaPublishExceptionTest {

    @Test
    void constructor_ShouldSetCorrelationId() {
        // Given
        String correlationId = "123-abc-456";
        String message = "Failed to publish message";
        Throwable cause = new RuntimeException("Connection refused");

        // When
        KafkaPublishException exception = new KafkaPublishException(message, cause, correlationId);

        // Then
        assertEquals(correlationId, exception.getCorrelationId());
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void getMessage_ShouldReturnCorrectMessage() {
        // Given
        String correlationId = "123-abc-456";
        String message = "Failed to publish message";

        // When
        KafkaPublishException exception = new KafkaPublishException(message, null, correlationId);

        // Then
        assertEquals(message, exception.getMessage());
    }

    @Test
    void getCause_ShouldReturnCorrectCause() {
        // Given
        String correlationId = "123-abc-456";
        Throwable cause = new RuntimeException("Connection refused");

        // When
        KafkaPublishException exception = new KafkaPublishException("Failed to publish message", cause, correlationId);

        // Then
        assertNotNull(exception.getCause());
        assertEquals("Connection refused", exception.getCause().getMessage());
    }
}
