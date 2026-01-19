package com.oguz.resilientstockservice.config;

import brave.sampler.Sampler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObservationConfig {

    // YAML dosyasına güvenmek yerine, kodu burada zorluyoruz.
    // ALWAYS_SAMPLE = %100 her isteği kaydet demek.
    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }

}
