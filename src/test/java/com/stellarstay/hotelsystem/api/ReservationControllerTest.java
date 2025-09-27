package com.stellarstay.hotelsystem.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stellarstay.hotelsystem.api.dto.CreateReservationRequest;
import com.stellarstay.hotelsystem.api.dto.ReservationResponse;
import com.stellarstay.hotelsystem.api.exception.BadRequestException;
import com.stellarstay.hotelsystem.api.exception.RoomNotAvailableException;
import com.stellarstay.hotelsystem.ports.in.ReservationUseCase;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;

@WebMvcTest(controllers = ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationUseCase reservationUseCase;

    @MockBean
    private MeterRegistry meterRegistry;

    @MockBean(name = "reservationTaskExecutor")
    private ThreadPoolTaskExecutor reservationTaskExecutor;

    @Autowired
    private ReservationController reservationController;

    private CreateReservationRequest validRequest;
    private ReservationResponse successResponse;

    @BeforeEach
    void setUp() {
        // Configurar el contador mock
        Counter mockCounter = mock(Counter.class);
        when(meterRegistry.counter("reservations_created_total")).thenReturn(mockCounter);

        // Inicializar el contador en el controlador real del contexto Spring
        ReflectionTestUtils.setField(reservationController, "reservationCreatedCounter", mockCounter);

        // Configurar el ejecutor para que ejecute las tareas inmediatamente
        // Para métodos void usamos doAnswer en lugar de thenAnswer
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(reservationTaskExecutor).execute(any(Runnable.class));

        // Configurar el ThreadPoolTaskExecutor para actuar como un ejecutor síncrono
        when(reservationTaskExecutor.getThreadPoolExecutor()).thenReturn(null);
        when(reservationTaskExecutor.submit(any(Runnable.class))).thenAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        });

        // Simular comportamiento asíncrono
        when(reservationTaskExecutor.submit(ArgumentMatchers.<Callable<?>>any())).thenAnswer(invocation -> {
            Callable<?> callable = invocation.getArgument(0);
            if (callable != null) {
                return CompletableFuture.completedFuture(callable.call());
            } else {
                return CompletableFuture.completedFuture(null);
            }
        });

        // Preparar datos de prueba
        validRequest = new CreateReservationRequest();
        validRequest.setRoomId(123L);
        validRequest.setGuestName("John Doe");
        validRequest.setGuests(2);
        validRequest.setCheckInDate(LocalDate.now().plusDays(1));
        validRequest.setCheckOutDate(LocalDate.now().plusDays(5));
        validRequest.setBreakfastIncluded(true);

        successResponse = new ReservationResponse();
        successResponse.setReservationId(123L);
        successResponse.setGuestName("John Doe");
        successResponse.setTotalPrice(500.0);
    }

    @Test
    void createReservation_ShouldReturnCreatedReservation() throws Exception {
        // Given
        when(reservationUseCase.createReservation(ArgumentMatchers.any(CreateReservationRequest.class)))
                .thenReturn(successResponse);

        // Configurar el comportamiento asíncrono
        when(reservationTaskExecutor.getThreadPoolExecutor()).thenReturn(null);
        when(reservationTaskExecutor.submit(any(Callable.class))).thenAnswer(invocation -> {
            Callable<?> callable = invocation.getArgument(0);
            Object result = callable.call();
            return CompletableFuture.completedFuture(result);
        });

        // When & Then
        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(
                mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                        .andReturn()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reservationId", is(123)))
                .andExpect(jsonPath("$.guestName", is("John Doe")))
                .andExpect(jsonPath("$.totalPrice", is(500.0)));
    }
}
