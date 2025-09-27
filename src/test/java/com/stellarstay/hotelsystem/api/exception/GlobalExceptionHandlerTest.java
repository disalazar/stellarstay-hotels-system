package com.stellarstay.hotelsystem.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleConstraintViolationException_returnsBadRequest() {
        ConstraintViolation<?> violation = Mockito.mock(ConstraintViolation.class);
        Mockito.when(violation.getMessage()).thenReturn("error");
        Set<ConstraintViolation<?>> violations = Collections.singleton(violation);
        ConstraintViolationException ex = new ConstraintViolationException(violations);
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        ResponseEntity<?> response = handler.handleConstraintViolationException(ex, req);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void handleMethodArgumentTypeMismatchException_returnsBadRequest() {
        MethodArgumentTypeMismatchException ex = Mockito.mock(MethodArgumentTypeMismatchException.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(ex.getName()).thenReturn("param");
        Mockito.when(ex.getRequiredType()).thenReturn((Class)String.class); // cast para evitar error de tipo
        Mockito.when(ex.getValue()).thenReturn(123);
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        ResponseEntity<?> response = handler.handleMethodArgumentTypeMismatchException(ex, req);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void handleGenericException_returnsInternalServerError() {
        Exception ex = new Exception("fail");
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        ResponseEntity<?> response = handler.handleGenericException(ex, req);
        assertEquals(500, response.getStatusCode().value());
    }
}
