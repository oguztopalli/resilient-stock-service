package com.oguz.resilientstockservice.config;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.grid.jcache.JCacheProxyManager;
import org.redisson.api.RedissonClient;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

@Configuration
public class RedisConfig {

    @Bean
    public ProxyManager<String> proxyManager(RedissonClient redissonClient) {
        // 1. JCache Provider'ı alıyoruz
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();

        // 2. DÜZELTME BURADA:
        // Değişken tipini 'RedissonConfiguration' yerine genel 'javax.cache.configuration.Configuration' yaptık.
        // Ayrıca Generics tipini <String, byte[]> olarak belirttik.
        javax.cache.configuration.Configuration<String, byte[]> config = RedissonConfiguration.fromInstance(redissonClient);

        // Cache'i alıyoruz veya oluşturuyoruz
        Cache<String, byte[]> cache = cacheManager.getCache("rate-limit-cache");
        if (cache == null) {
            cache = cacheManager.createCache("rate-limit-cache", config);
        }

        // 3. Bucket4j Yöneticisini döndürüyoruz
        return new JCacheProxyManager<>(cache);
    }
}