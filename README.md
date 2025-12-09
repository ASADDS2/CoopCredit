# CoopCredit Microservices Platform

A modern, cloud-native credit application platform built with **Java 21**, **Spring Boot 3.5**, and **Angular**. The system follows **Hexagonal Architecture** and uses a microservices approach with Docker orchestration.

## 🏗️ System Architecture

The platform consists of the following components:

| Service | Technology | Port | Description |
|---------|------------|------|-------------|
| **Creddit Application** | Java / Spring Boot | `8080` | Core domain logic, auth, and credit management. |
| **Risk Central Mock** | Java / Spring Boot | `8081` | Simulates external risk evaluation agency. |
| **Frontend** | Angular 20 | `4200` | Web interface for users and admins. |
| **PostgreSQL** | Database | `5433` | Primary data store. |
| **Prometheus** | Monitoring | `19090` | Metrics collection. |
| **Grafana** | Visualization | `3000` | Dashboards and alerts. |

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 21 (for local dev)
- Node.js & npm (for frontend dev)

### Run Everything (Docker)
To start the entire ecosystem (Backend, Mock, DB, Monitoring):

```bash
./start.sh
```
Or manually:
```bash
docker-compose up -d --build
```

### Stop Services
```bash
./stop.sh
```

## 🔗 Access Points

- **Frontend Application**: [http://localhost:4200](http://localhost:4200)
- **Creddit Service API**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Risk Service API**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **Grafana Dashboards**: [http://localhost:3000](http://localhost:3000) (User: `admin` / Pass: `admin`)
- **Prometheus**: [http://localhost:19090](http://localhost:19090)

## 📂 Project Structure

```text
CoopCredit/
├── creddit-application-service/  # Main Domain Service (Hexagonal)
├── risk-central-mock-service/    # External Dependency Mock
├── front-end-web/                # Angular Frontend
├── monitoring/                   # Prometheus Config
├── docker-compose.yml            # Orchestration
├── start.sh                      # Startup Script
└── stop.sh                       # Shutdown Script
```

## 🧪 Testing

Run unit and integration tests for the backend services:
```bash
cd creddit-application-service
./mvnw test
```