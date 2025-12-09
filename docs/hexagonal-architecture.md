# Hexagonal Architecture (Ports & Adapters)

This service follows a strict hexagonal architecture to keep the domain isolated from frameworks.

## Layers

- Domain: Entities, Value Objects, Exceptions, Ports (IN/OUT). No framework dependencies.
- Application: Use Cases orchestrating domain rules through ports.
- Infrastructure: Web (REST controllers), Persistence (JPA), External adapters (Risk Central REST).

## Diagram

```mermaid
flowchart TD
  UI[REST Controllers] --> UC[Use Cases]
  UC -->|IN Ports| DOM[Domain]
  DOM -->|OUT Ports| ADP[Adapters]
  ADP --> DB[(PostgreSQL)]
  ADP --> RISK[Risk Central Mock]
```

## Ports

- IN Ports: `RegisterAffiliateUseCase`, `RegisterCreditApplicationUseCase`, `EvaluateCreditApplicationUseCase`, etc.
- OUT Ports: `AffiliateRepositoryPort`, `CreditApplicationRepositoryPort`, `RiskCentralPort`, `UserRepositoryPort`.

## Benefits

- Testable domain logic.
- Swappable adapters (e.g., switch mock risk service with real one).
- Framework changes do not leak into the domain.
