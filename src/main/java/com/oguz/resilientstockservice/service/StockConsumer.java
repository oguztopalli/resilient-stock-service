package com.oguz.resilientstockservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

@Service
public class StockConsumer {

    private static final Logger logger = Logger.getLogger(StockConsumer.class.getName());

    // groupId: application.yml'da verdiğimiz isimle aynı olmalı
    @KafkaListener(topics = "stock-updates", groupId = "stock-group")
    @Transactional
    public void consumeMessage(String message) {
        logger.info(">>> KAFKA TÜKETİCİSİ YAKALADI: " + message);
        
        // Buraya "Thread.sleep(5000)" bile koysan, kullanıcıyı etkilemez!
        // Çünkü bu işlem tamamen arka planda (Asenkron) dönüyor.
        processMessageInBackground(message);

        logger.info(">>> [TRANSACTION BAŞLADI] Mesaj alındı: " + message);
        
        // Veritabanı işlemini çağırıyoruz
        processMessageInDB(message);

        logger.info("<<< [TRANSACTION COMMIT] İşlem başarıyla bitti.");
    }

    private void processMessageInDB(String msg) {
        // Veritabanı yazma simülasyonu
        try {
            // Burası normalde repository.save(entity) olur.
            logger.info("... Veritabanına yazılıyor ...");
            
            // HATA SİMÜLASYONU (Test etmek için)
            // Eğer mesajın içinde "FAIL" kelimesi varsa patlat!
            if (msg.contains("FAIL")) {
                throw new RuntimeException("Veritabanı bağlantısı koptu!");
            }

            Thread.sleep(1000); 

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Not: RuntimeException fırlatıldığında Spring otomatik Rollback yapar.
    }

    private void processMessageInBackground(String msg) {
        try {
            // Veritabanına yazma simülasyonu (2 saniye sürsün)
            Thread.sleep(2000); 
            logger.info("--- [DB SAVE] Veri başarıyla işlendi ve kaydedildi: " + msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
