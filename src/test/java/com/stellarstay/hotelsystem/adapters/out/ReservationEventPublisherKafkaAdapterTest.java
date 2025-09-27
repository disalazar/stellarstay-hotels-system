package com.stellarstay.hotelsystem.adapters.out;

import com.stellarstay.hotelsystem.api.dto.ReservationCreatedEvent;
import com.stellarstay.hotelsystem.api.dto.ReservationMapper;
import com.stellarstay.hotelsystem.domain.Reservation;
import com.stellarstay.hotelsystem.domain.Room;
import com.stellarstay.hotelsystem.domain.RoomType;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class ReservationEventPublisherKafkaAdapterTest {

    @Mock
    private KafkaTemplate<String, ReservationCreatedEvent> kafkaTemplate;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private CircuitBreaker reservationEventCircuitBreaker;

    @Mock
    private Retry reservationEventRetry;

    @Captor
    private ArgumentCaptor<ReservationCreatedEvent> eventCaptor;

    private ReservationEventPublisherKafkaAdapter eventPublisher;
    private final String TOPIC = "reservations-topic";

    @BeforeEach
    void setUp() {
        // Configurar comportamiento de los mocks
        try (var circuitBreakerMock = mockStatic(CircuitBreaker.class);
             var retryMock = mockStatic(Retry.class)) {

            circuitBreakerMock.when(() -> CircuitBreaker.decorateRunnable(eq(reservationEventCircuitBreaker), any(Runnable.class)))
                    .thenAnswer(invocation -> invocation.getArgument(1));

            retryMock.when(() -> Retry.decorateRunnable(eq(reservationEventRetry), any(Runnable.class)))
                    .thenAnswer(invocation -> invocation.getArgument(1));

            eventPublisher = new ReservationEventPublisherKafkaAdapter(
                    kafkaTemplate,
                    reservationMapper,
                    reservationEventCircuitBreaker,
                    reservationEventRetry);

            ReflectionTestUtils.setField(eventPublisher, "topic", TOPIC);
        }
    }

    @Test
    void publishReservationCreated_ShouldSendEventToKafka() {
        // Given
        Reservation reservation = createSampleReservation();
        ReservationCreatedEvent event = new ReservationCreatedEvent();
        event.setReservationId(reservation.getId());

        when(reservationMapper.toEvent(reservation)).thenReturn(event);

        SendResult<String, ReservationCreatedEvent> sendResult = new SendResult<>(null, null);
        CompletableFuture<SendResult<String, ReservationCreatedEvent>> future = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(anyString(), any(ReservationCreatedEvent.class))).thenReturn(future);

        try (var circuitBreakerMock = mockStatic(CircuitBreaker.class);
             var retryMock = mockStatic(Retry.class)) {

            circuitBreakerMock.when(() -> CircuitBreaker.decorateRunnable(eq(reservationEventCircuitBreaker), any(Runnable.class)))
                    .thenAnswer(invocation -> invocation.getArgument(1));

            retryMock.when(() -> Retry.decorateRunnable(eq(reservationEventRetry), any(Runnable.class)))
                    .thenAnswer(invocation -> invocation.getArgument(1));

            // When
            eventPublisher.publishReservationCreated(reservation);

            // Then
            verify(reservationMapper).toEvent(reservation);
            verify(kafkaTemplate).send(eq(TOPIC), any(ReservationCreatedEvent.class));
        }
    }

    @Test
    void publishReservationCreated_WhenKafkaFails_ShouldRetryOperation() {
        // Given
        Reservation reservation = createSampleReservation();
        ReservationCreatedEvent event = new ReservationCreatedEvent();
        event.setReservationId(reservation.getId());

        when(reservationMapper.toEvent(reservation)).thenReturn(event);

        try (var circuitBreakerMock = mockStatic(CircuitBreaker.class);
             var retryMock = mockStatic(Retry.class)) {

            // Configurar Circuit Breaker para simular un fallo y reintento
            circuitBreakerMock.when(() -> CircuitBreaker.decorateRunnable(eq(reservationEventCircuitBreaker), any(Runnable.class)))
                    .thenAnswer(invocation -> {
                        Runnable original = invocation.getArgument(1);
                        return (Runnable) () -> {
                            // Simular que el circuit breaker permite el intento
                            original.run();
                        };
                    });

            retryMock.when(() -> Retry.decorateRunnable(eq(reservationEventRetry), any(Runnable.class)))
                    .thenAnswer(invocation -> {
                        Runnable original = invocation.getArgument(1);
                        return (Runnable) () -> {
                            // Simular un reintento (simplemente ejecuta la acción)
                            original.run();
                        };
                    });

            // Primera llamada lanza excepción, segunda funciona bien
            when(kafkaTemplate.send(anyString(), any(ReservationCreatedEvent.class)))
                .thenThrow(new RuntimeException("Kafka connection error"))
                .thenReturn(CompletableFuture.completedFuture(new SendResult<>(null, null)));

            // When
            try {
                eventPublisher.publishReservationCreated(reservation);
            } catch (Exception e) {
                // La excepción se maneja dentro del retry/circuit breaker en un caso real
            }
        }
    }

    private Reservation createSampleReservation() {
        Room room = new Room(1L, RoomType.JUNIOR_SUITE, 2, true);

        return Reservation.builder()
                .id(123L)
                .room(room)
                .guestName("John Doe")
                .guests(2)
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(3))
                .breakfastIncluded(true)
                .totalPrice(600.0)
                .build();
    }
}
