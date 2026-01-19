package com.oguz.resilientstockservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker; 
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.logging.Logger;

import org.springframework.kafka.core.KafkaTemplate;

@Service
@RequiredArgsConstructor
public class ExternalStockService {

    private final Random random = new Random();
    private static final Logger logger = Logger.getLogger(ExternalStockService.class.getName());
    private final ObservationRegistry observationRegistry;
    private final KafkaTemplate<String, String> kafkaTemplate;


    // RETRY-CIRCUIT BREAKER
    // name="stock-api": Config dosyası
    @CircuitBreaker(name = "stock-api", fallbackMethod = "fallbackStockPrice") 
    @Retry(name = "stock-api", fallbackMethod = "fallbackStockPrice")
    public String getStockPrice(String symbol) {
    //Observation nesnesi yaratıyoruz "external-stock-service" -> Zipkin'de görünecek isim

        return Observation.createNotStarted("external-stock-service", observationRegistry)
                .observe(() -> {
                    logger.info("--- Dış Servise İstek Atılıyor --- " + symbol);
                    simulateLatency();
                    simulateFailure();
                    
                    String result = symbol.toUpperCase() + ": " + (100 + random.nextInt(50)) + "$";

                    // --- KAFKA MESAJI GÖNDERME ---
                    // "stock-updates" adında bir konuya (Topic) mesaj atıyoruz
                    kafkaTemplate.send("stock-updates", symbol, result);
                    logger.info("Kafka'ya mesaj atıldı: " + result);
                    // -----------------------------

                    return result;
                });
    }

    // FALLBACK METODU (Ortak Kullanım)
    // Hem Retry tükendiğinde hem de Circuit Breaker "AÇIK" (Open) olduğunda burası çalışır.
    public String fallbackStockPrice(String symbol, Throwable t) {
        logger.warning("Fallback Devrede: " + t.getMessage());
        return "Cache: " + symbol.toUpperCase() + ": 100$";
    }

    private void simulateFailure() {
        if (random.nextInt(10) < 5) { // %50 hata
            throw new RuntimeException("Dış Servis yanıt vermiyor!");
        }
    }

    private void simulateLatency() {
        try { Thread.sleep(random.nextInt(500)); } catch (InterruptedException e) {}
    }

}