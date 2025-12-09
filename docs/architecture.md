# System Architecture

This project follows a microservices architecture with Hexagonal style (Ports & Adapters) in the main domain service.

## Components

- Creddit Application Service (Java / Spring Boot, port 8080)
- Risk Central Mock Service (Java / Spring Boot, port 8081)
- PostgreSQL (Database, external port 5433)
- Prometheus (Metrics, port 19090)
- Grafana (Dashboards, port 3000)

## Microservices Diagram

```mermaid
flowchart LR
  subgraph User[User]
  end

  subgraph App[Creddit Application Service]
    A1[REST Controllers]
    A2[Use Cases]
    A3[Domain (Entities, Ports)]
    A4[Adapters: JPA, Risk REST]
  end

  DB[(PostgreSQL)]
  Risk[Risk Central Mock]
  Prom[Prometheus]
  Graf[Grafana]

  User -->|HTTP| A1
  A1 --> A2 --> A3 --> A4
  A4 -->|JPA| DB
  A4 -->|REST| Risk
  Prom <-->|/actuator/prometheus| App
  Prom <-->|/actuator/prometheus| Risk
  Graf -->|scrape| Prom
```

## Hexagonal Style (Ports & Adapters)

- Domain without framework dependencies (POJOs, exceptions, Ports IN/OUT).
- Application implements use cases and orchestrates rules.
- Infrastructure exposes web controllers, JPA persistence, and external adapters (risk-central).

## Security

- Stateless JWT authentication.
- Supported roles: `ROLE_AFILIADO`, `ROLE_ANALISTA`, `ROLE_ADMIN`.
- Authorization via `@PreAuthorize` and validations in use cases.

## Observability

- Spring Boot Actuator (health, info, metrics, prometheus).
- Micrometer for metrics.
- Prometheus + Grafana for visualization.

## Resilience

- Resilience4j (Circuit Breaker + Retry) in the REST adapter to Risk Central.
- Conservative fallback in case of unavailability.
