package com.oguz.resilientstockservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.oguz.resilientstockservice.interceptor.RateLimitInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Tüm /api/** isteklerine rate limit uygula
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/**");
    }
}
