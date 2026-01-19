package com.oguz.resilientstockservice.interceptor;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.oguz.resilientstockservice.service.RateLimiterService;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RateLimiterService rateLimiterService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // İstemciyi tanıyalım (Header'da X-API-KEY olduğunu varsayıyoruz veya IP alabiliriz)
        String apiKey = request.getHeader("X-API-KEY");
        
        if (apiKey == null || apiKey.isEmpty()) {
            // Demo için API key yoksa IP adresini kullanabiliriz
            apiKey = request.getRemoteAddr();
        }

        System.out.println(">>> İstek Geldi! URL: " + request.getRequestURI());
        System.out.println(">>> Kullanıcı Key: " + apiKey);

        Bucket tokenBucket = rateLimiterService.resolveBucket(apiKey);

        // 1 jeton harcamayı dene
        ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            System.out.println(">>> BAŞARILI. Kalan Hak: " + probe.getRemainingTokens()); 
            // Başarılı, kalan hakkı header'a ekle (Kullanıcı dostu yaklaşım)
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;
        } else {
            System.out.println(">>> LİMİT DOLDU! Engellendi.");
            // Başarısız, limit doldu.
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Çok fazla istek attınız. Lütfen bekleyin.");
            return false;
        }
    }

}

