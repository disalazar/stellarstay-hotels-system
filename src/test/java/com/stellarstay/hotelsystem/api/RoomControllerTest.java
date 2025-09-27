package com.stellarstay.hotelsystem.api;

import com.stellarstay.hotelsystem.api.dto.RoomAvailabilityRequest;
import com.stellarstay.hotelsystem.api.dto.RoomResponse;
import com.stellarstay.hotelsystem.api.exception.BadRequestException;
import com.stellarstay.hotelsystem.api.validation.AvailableRoomsRequestValidator;
import com.stellarstay.hotelsystem.domain.RoomType;
import com.stellarstay.hotelsystem.ports.in.RoomAvailabilityUseCase;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RoomController.class)
@ActiveProfiles("test")
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomAvailabilityUseCase roomAvailabilityUseCase;

    @MockBean
    private AvailableRoomsRequestValidator validator;

    @MockBean
    private MeterRegistry meterRegistry;

    @Autowired
    private RoomController roomController;

    private List<RoomResponse> availableRooms;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    @BeforeEach
    void setUp() {
        // Configurar el contador mock
        Counter mockCounter = mock(Counter.class);
        when(meterRegistry.counter("available_rooms_queried_total")).thenReturn(mockCounter);

        // Inicializar el contador en el controlador real del contexto Spring
        ReflectionTestUtils.setField(roomController, "availableRoomsQueriedCounter", mockCounter);

        // Configurar fechas de prueba
        checkInDate = LocalDate.now().plusDays(1);
        checkOutDate = LocalDate.now().plusDays(5);

        // Preparar datos de respuesta usando los tipos de habitación correctos
        RoomResponse juniorSuiteRoom = RoomResponse.builder()
                .roomId(1L)
                .type("JUNIOR_SUITE")
                .capacity(2)
                .available(true)
                .build();

        RoomResponse kingSuiteRoom = RoomResponse.builder()
                .roomId(2L)
                .type("KING_SUITE")
                .capacity(3)
                .available(true)
                .build();

        availableRooms = Arrays.asList(juniorSuiteRoom, kingSuiteRoom);

        // Configurar comportamiento por defecto del validador
        doNothing().when(validator).validate(
                ArgumentMatchers.any(String.class),
                ArgumentMatchers.any(LocalDate.class),
                ArgumentMatchers.any(LocalDate.class),
                ArgumentMatchers.anyInt());
    }

    @Test
    void getAvailableRooms_ShouldReturnAllAvailableRooms() throws Exception {
        // Given
        when(roomAvailabilityUseCase.findAvailableRooms(any(RoomAvailabilityRequest.class)))
                .thenReturn(availableRooms);

        // When & Then
        mockMvc.perform(get("/api/rooms/available")
                .param("checkInDate", checkInDate.toString())
                .param("checkOutDate", checkOutDate.toString())
                .param("guests", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].roomId", is(1)))
                .andExpect(jsonPath("$[0].type", is("JUNIOR_SUITE")))
                .andExpect(jsonPath("$[0].capacity", is(2)))
                .andExpect(jsonPath("$[1].roomId", is(2)))
                .andExpect(jsonPath("$[1].type", is("KING_SUITE")));
    }

    @Test
    void getAvailableRooms_WithTypeFilter_ShouldReturnFilteredRooms() throws Exception {
        // Given
        RoomResponse kingSuiteRoom = availableRooms.get(1); // Solo la habitación King Suite
        when(roomAvailabilityUseCase.findAvailableRooms(any(RoomAvailabilityRequest.class)))
                .thenReturn(Collections.singletonList(kingSuiteRoom));

        // When & Then
        mockMvc.perform(get("/api/rooms/available")
                .param("type", "KING_SUITE")
                .param("checkInDate", checkInDate.toString())
                .param("checkOutDate", checkOutDate.toString())
                .param("guests", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].roomId", is(2)))
                .andExpect(jsonPath("$[0].type", is("KING_SUITE")));
    }

    @Test
    void getAvailableRooms_WithoutAvailableRooms_ShouldReturnEmptyList() throws Exception {
        // Given
        when(roomAvailabilityUseCase.findAvailableRooms(any(RoomAvailabilityRequest.class)))
                .thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/rooms/available")
                .param("checkInDate", checkInDate.toString())
                .param("checkOutDate", checkOutDate.toString())
                .param("guests", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAvailableRooms_WithInvalidRoomType_ShouldReturnBadRequest() throws Exception {
        // Given
        doThrow(new BadRequestException("Invalid room type: INVALID_TYPE", "test-correlation-id"))
                .when(validator).validate(
                        ArgumentMatchers.eq("INVALID_TYPE"),
                        ArgumentMatchers.any(LocalDate.class),
                        ArgumentMatchers.any(LocalDate.class),
                        ArgumentMatchers.anyInt());

        // When & Then
        mockMvc.perform(get("/api/rooms/available")
                .param("type", "INVALID_TYPE") // Tipo de habitación inválido
                .param("checkInDate", checkInDate.toString())
                .param("checkOutDate", checkOutDate.toString())
                .param("guests", "2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Invalid room type: INVALID_TYPE")));
    }
}
