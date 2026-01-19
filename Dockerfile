# 1. Base Image: Java 17 yüklü hafif bir Linux
FROM eclipse-temurin:17-jdk-jammy

# 2. JAR dosyasının nereye kopyalanacağı
ARG JAR_FILE=target/*.jar

# 3. JAR'ı konteynerin içine 'app.jar' olarak kopyala
COPY ${JAR_FILE} app.jar

# 4. Uygulama başlatma komutu
ENTRYPOINT ["java", "-jar", "/app.jar"]