package com.stellarstay.hotelsystem;

import com.stellarstay.hotelsystem.adapters.out.ReservationEventPublisherKafkaAdapter;
import com.stellarstay.hotelsystem.config.KafkaConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class StellarstayHotelsSystemApplicationTests {

    @MockBean
    private KafkaConfig kafkaConfig;
    @MockBean
    private ReservationEventPublisherKafkaAdapter reservationEventPublisherKafkaAdapter;

    @Test
    void contextLoads() {
    }
}
