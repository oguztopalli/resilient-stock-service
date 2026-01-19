# 📈 Resilient Stock Service

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-green?style=for-the-badge&logo=springboot)
![Kafka](https://img.shields.io/badge/Apache_Kafka-Event_Driven-black?style=for-the-badge&logo=apachekafka)
![Redis](https://img.shields.io/badge/Redis-Rate_Limiting-red?style=for-the-badge&logo=redis)
![Docker](https://img.shields.io/badge/Docker-Ready-blue?style=for-the-badge&logo=docker)

A robust, fault-tolerant, and event-driven microservice designed to handle high-throughput stock market data simulations. 
This project demonstrates **Enterprise-Grade** architecture patterns including Distributed Rate Limiting, Circuit Breakers, Asynchronous Messaging, and Distributed Tracing.

## 🏗️ Architecture Overview

The system is built to withstand failures and high traffic loads using the following flow:

1.  **Client Request:** Incoming HTTP requests are intercepted by a **Distributed Rate Limiter**.
2.  **Service Layer:** The service attempts to fetch stock data (simulated external API).
3.  **Resilience:** **Resilience4j** protects the system using Circuit Breaker and Retry patterns.
4.  **Async Processing:** Successful data is published to **Apache Kafka**.
5.  **Consumer:** A separate consumer listens to Kafka topics and processes data (e.g., DB save) with **Transactional Integrity**.
6.  **Observability:** Every step is traced using **Micrometer & Zipkin**.

---

## 🚀 Key Features

### 🛡️ 1. Distributed Rate Limiting (Redis + Bucket4j)
Prevents system overload and DoS attacks.
- Uses **Token Bucket Algorithm**.
- State is stored in **Redis**, making it scalable across multiple instances.
- *Logic:* Limits users to X requests per minute globally.

### ⚡ 2. Fault Tolerance (Resilience4j)
Ensures the system stays responsive even when external services fail.
- **Circuit Breaker:** "Fails fast" if the error rate exceeds a threshold, preventing cascading failures.
- **Retry:** Automatically retries failed operations for transient network glitches.

### 📨 3. Event-Driven Architecture (Apache Kafka)
Decouples the request processing from data persistence.
- **Producer:** Sends stock updates to a Kafka topic immediately after fetching.
- **Consumer:** Asynchronously processes messages to ensure low latency for the user.

### 🔍 4. Observability & Distributed Tracing
Full visibility into the request lifecycle.
- **Micrometer Tracing:** Assigns unique Trace IDs and Span IDs.
- **Zipkin:** Visualizes the latency and dependency graph of requests.

### 💾 5. Data Consistency & Transactions
- **@Transactional:** Ensures that Kafka message consumption and Database operations are atomic. If the DB write fails, the transaction rolls back to prevent data loss.

---

## 🛠️ Tech Stack

- **Language:** Java 17 (Eclipse Temurin)
- **Framework:** Spring Boot 3
- **Database/Cache:** Redis
- **Messaging:** Apache Kafka, Zookeeper
- **Resilience:** Resilience4j
- **Tracing:** Micrometer, Zipkin
- **Containerization:** Docker, Docker Compose

---

## 🐳 Getting Started (Docker)

You can run the entire infrastructure (App + Kafka + Redis + Zipkin) with a single command.

### Prerequisites
- Docker Desktop installed and running.

### Installation

1. **Clone the repository**
   git clone [https://github.com/oguztopalli/resilient-stock-service.git]
   cd resilient-stock-service
2. **Run with Docker Compose**
   docker compose up -d --build
   This will build the Java JAR file and start all containers.
3.	**Check Status**
   docker ps
    
 ### API Endpoints
   Get Stock Price
   Fetches the stock price, triggers the circuit breaker logic, and sends an event to Kafka.
   HTTP
   GET http://localhost:8080/api/stocks/{symbol}
   Example Request:
   Bash
   curl http://localhost:8080/api/stocks/AAPL
   Response:
   JSON
   "AAPL: 145$"
    
### Monitoring & Visualization
   Once the application is running, you can access the following dashboards:
   Service	URL	Description
   Zipkin UI	http://localhost:9411
   View distributed traces and latency waterfalls.
   App API	http://localhost:8080
   The main application endpoint.
    
### Testing Scenarios
   1.	Rate Limiting: Send 10+ requests rapidly. You will receive 429 Too Many Requests.
   2.	Circuit Breaker: The system simulates random failures. Check logs to see CircuitBreaker 'stock-api' changed state from CLOSED to OPEN.
   3.	Async Transaction: Check application logs (docker logs -f stock-service) to see the Producer sending and Consumer receiving messages.
    
### Author
   Oğuz Topallı
   •	[LinkedIn Profile](https://www.linkedin.com/in/oguztopalli/)
   •	[GitHub Profile](https://github.com/oguztopalli)
   