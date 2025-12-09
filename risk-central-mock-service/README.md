# Risk Central Mock Service

A lightweight microservice designed to mock an external Risk Evaluation Agency (Central de Riesgos). It provides risk assessments for credit applicants based on simulated logic.

## 🎯 Purpose
This service simulates external dependencies to allow independent testing and development of the `creddit-application-service` without relying on real third-party APIs.

## 🛠️ Tech Stack
- **Java 21**
- **Spring Boot 3.5.0**
- **Spring Boot Actuator**
- **SpringDoc OpenAPI**

## ⚙️ Logic
The service evaluates risk based on simple rules (e.g., random scores or deterministic logic based on document ID) to return:
- **Score**: 0-999
- **Risk Level**: LOW, MEDIUM, HIGH

## 🚀 Getting Started

### Running Locally
```bash
./mvnw spring-boot:run
```
Runs on port **8081** by default.

### API Documentation
- Swagger UI: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

## 📡 Endpoints
- **POST** `/risk-evaluation`: Evaluates risk for a given user.
  ```json
  {
    "documentId": "12345678",
    "amount": 5000.00,
    "term": 12
  }
  ```
