# Hexagonal Architecture - CoopCredit

## Hexagonal Architecture Diagram

```mermaid
graph TB
    subgraph "Infrastructure Layer - Adapters"
        subgraph "Input Adapters (Driving)"
            REST[REST Controllers]
            SWAGGER[Swagger UI]
        end
        
        subgraph "Output Adapters (Driven)"
            JPA[JPA Repository]
            RISK[Risk Central Client]
            JWT[JWT Provider]
        end
    end
    
    subgraph "Application Layer"
        UC1[RegisterAffiliate UseCase]
        UC2[RegisterCreditApplication UseCase]
        UC3[EvaluateCreditApplication UseCase]
        UC4[AuthenticateUser UseCase]
    end
    
    subgraph "Domain Layer - Core"
        subgraph "Domain Models"
            AFFILIATE[Affiliate]
            CREDIT[CreditApplication]
            RISKEVAL[RiskEvaluation]
            USER[User]
        end
        
        subgraph "Domain Ports"
            subgraph "Input Ports"
                IP1[RegisterAffiliateUseCase]
                IP2[RegisterCreditApplicationUseCase]
                IP3[EvaluateCreditApplicationUseCase]
                IP4[AuthenticateUserUseCase]
            end
            
            subgraph "Output Ports"
                OP1[AffiliateRepositoryPort]
                OP2[CreditApplicationRepositoryPort]
                OP3[RiskEvaluationPort]
                OP4[UserRepositoryPort]
            end
        end
        
        subgraph "Domain Services"
            VS[ValidationService]
            CS[CalculationService]
        end
    end
    
    REST --> UC1
    REST --> UC2
    REST --> UC3
    REST --> UC4
    
    UC1 --> IP1
    UC2 --> IP2
    UC3 --> IP3
    UC4 --> IP4
    
    IP1 --> AFFILIATE
    IP2 --> CREDIT
    IP3 --> RISKEVAL
    IP4 --> USER
    
    UC1 --> OP1
    UC2 --> OP2
    UC2 --> OP3
    UC3 --> OP3
    UC4 --> OP4
    
    OP1 --> JPA
    OP2 --> JPA
    OP3 --> RISK
    OP4 --> JPA
    
    VS --> AFFILIATE
    VS --> CREDIT
    CS --> CREDIT
    CS --> RISKEVAL
```

## Layer Description

### 1. Domain Layer (Core)

- **Entities**: Pure POJOs without framework dependencies
  - `Affiliate`: Represents a cooperative member
  - `CreditApplication`: Credit application request
  - `RiskEvaluation`: Risk assessment result
  - `User`: System user

- **Input Ports** (Interfaces defining use cases):
  - `RegisterAffiliateUseCase`
  - `RegisterCreditApplicationUseCase`
  - `EvaluateCreditApplicationUseCase`
  - `AuthenticateUserUseCase`

- **Output Ports** (Interfaces for external services):
  - `AffiliateRepositoryPort`
  - `CreditApplicationRepositoryPort`
  - `RiskEvaluationPort`
  - `UserRepositoryPort`

### 2. Application Layer

- **Use Cases**: Business logic implementations
  - Orchestrate domain operations
  - Contain no infrastructure logic
  - Implement input ports

### 3. Infrastructure Layer

- **Input Adapters**:
  - REST Controllers: Expose HTTP API
  - Swagger UI: Interactive documentation

- **Output Adapters**:
  - JPA Repositories: PostgreSQL persistence
  - Risk Central Client: External service integration
  - JWT Provider: Security token handling

## Hexagonal Architecture Principles

1. **Dependency Inversion**: Domain does not depend on infrastructure
2. **Domain Isolation**: Business logic is protected from external changes
3. **Testability**: Easy testing through port mocks
4. **Flexibility**: Adapters can change without affecting the domain

## Data Flow

1. HTTP Request → REST Controller
2. Controller → Application Use Case
3. Use Case → Domain Port (Input)
4. Domain Logic Execution
5. Domain → Repository Port (Output)
6. Repository Adapter → Database
7. Response → Controller → HTTP Response
