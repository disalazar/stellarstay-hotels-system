package com.stellarstay.hotelsystem.config;

import com.stellarstay.hotelsystem.domain.PriceCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {
    @Bean
    public PriceCalculator priceCalculator() {
        return new PriceCalculator();
    }
}
