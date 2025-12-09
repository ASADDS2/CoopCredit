# Creddit Application Service

This is the core microservice for the CoopCredit platform, responsible for managing credit applications, user authentication, and affiliate management. It follows a **Hexagonal Architecture** (Ports and Adapters) to ensure domain logic isolation.

## 🛠️ Tech Stack
- **Java 21**
- **Spring Boot 3.5.0**
- **PostgreSQL** (Database)
- **Flyway** (Database Migrations)
- **Spring Security + JWT** (Authentication)
- **Resilience4j** (Circuit Breaker)
- **Spring Boot Actuator** (Observability)
- **SpringDoc OpenAPI** (Documentation)

## 🏗️ Architecture
The project is structured following Hexagonal Architecture principles:
- **Domain**: Pure Java business logic (Entities, Ports, Exceptions). No framework dependencies.
- **Application**: Use Cases implementation. Orchestrates domain logic.
- **Infrastructure**: Adapters (Web, Persistence, External Services), Configuration.

## 🚀 Getting Started

### Prerequisites
- Java 21
- Maven
- Docker (optional, for DB)

### Running Locally
1. Ensure PostgreSQL is running (or update `application.yaml`).
2. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### API Documentation
Once running, access the Swagger UI:
- [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## 🔑 Key Endpoints
- **Auth**: `/api/auth/register`, `/api/auth/login`
- **Credit Applications**: `/api/credit-applications`
- **Affiliates**: `/api/affiliates`

## 📊 Observability
Metrics are exposed via Actuator for Prometheus scraping:
- Health: `/actuator/health`
- Metrics: `/actuator/prometheus`
