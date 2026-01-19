package com.oguz.resilientstockservice.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;

import io.github.bucket4j.distributed.proxy.ProxyManager; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

@Service
public class RateLimiterService {

    @Autowired
    private ProxyManager<String> proxyManager;

    public Bucket resolveBucket(String apiKey) {
        String key = "rate_limit:" + apiKey;
        Supplier<BucketConfiguration> configSupplier = this::getConfig;
        
        // ProxyManager üzerinden bucket'ı alıyoruz
        return proxyManager.builder().build(key, configSupplier);
    }

    private BucketConfiguration getConfig() {
        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        
        return BucketConfiguration.builder()
                .addLimit(limit)
                .build();
    }
}