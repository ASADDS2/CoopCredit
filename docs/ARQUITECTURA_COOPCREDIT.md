# 📚 Documentación Técnica - CoopCredit System

## Índice
1. [Visión General del Sistema](#visión-general-del-sistema)
2. [Microservicios](#microservicios)
3. [Arquitectura Hexagonal](#arquitectura-hexagonal)
4. [Dockerfiles](#dockerfiles)
5. [Render.yaml - Configuración de Despliegue](#renderyaml---configuración-de-despliegue)
6. [Principios SOLID](#principios-solid)
7. [Patrones de Diseño](#patrones-de-diseño)

---

## Visión General del Sistema

CoopCredit es un sistema de gestión de solicitudes de crédito para una cooperativa, compuesto por **2 microservicios** que se comunican entre sí:

```
┌─────────────────────────────────────────────────────────────────┐
│                        COOPCREDIT SYSTEM                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────────────┐       ┌──────────────────────────┐   │
│  │  creddit-application │       │  risk-central-mock       │   │
│  │      -service        │◄─────►│      -service            │   │
│  │     (Puerto 8080)    │ HTTP  │     (Puerto 8081)        │   │
│  └──────────┬───────────┘       └──────────────────────────┘   │
│             │                                                    │
│             ▼                                                    │
│  ┌──────────────────────┐                                       │
│  │     PostgreSQL       │                                       │
│  │   (Puerto 5432/5433) │                                       │
│  └──────────────────────┘                                       │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Microservicios

### 1. Creddit Application Service (Puerto 8080)

**Propósito:** Microservicio principal que gestiona las solicitudes de crédito, usuarios y afiliados.

**Estructura de Capas (Arquitectura Hexagonal):**

```
creddit-application-service/
└── src/main/java/com/coopcredit/creddit_application_service/
    ├── CredditApplicationServiceApplication.java  # Punto de entrada
    ├── domain/                    # 🎯 CAPA DE DOMINIO (Núcleo)
    │   ├── model/                 # Entidades de negocio
    │   │   ├── User.java
    │   │   ├── Affiliate.java
    │   │   ├── CreditApplication.java
    │   │   └── RiskEvaluation.java
    │   ├── ports/
    │   │   ├── in/                # Puertos de entrada (interfaces de casos de uso)
    │   │   │   ├── auth/
    │   │   │   │   ├── RegisterUserUseCase.java
    │   │   │   │   └── AuthenticateUserUseCase.java
    │   │   │   ├── affiliates/
    │   │   │   │   ├── RegisterAffiliateUseCase.java
    │   │   │   │   ├── GetAffiliateUseCase.java
    │   │   │   │   └── UpdateAffiliateUseCase.java
    │   │   │   └── creditapplications/
    │   │   │       ├── RegisterCreditApplicationUseCase.java
    │   │   │       ├── EvaluateCreditApplicationUseCase.java
    │   │   │       ├── GetCreditApplicationUseCase.java
    │   │   │       └── GetPendingApplicationsUseCase.java
    │   │   └── out/               # Puertos de salida (interfaces de repositorios)
    │   │       ├── UserRepositoryPort.java
    │   │       ├── AffiliateRepositoryPort.java
    │   │       ├── CreditApplicationRepositoryPort.java
    │   │       └── RiskCentralPort.java
    │   └── exception/             # Excepciones de dominio
    │       ├── NotFoundException.java
    │       ├── ConflictException.java
    │       └── BusinessException.java
    │
    ├── application/               # 🔄 CAPA DE APLICACIÓN
    │   └── usecases/              # Implementaciones de casos de uso
    │       ├── auth/
    │       │   ├── RegisterUserUseCaseImpl.java
    │       │   └── AuthenticateUserUseCaseImpl.java
    │       ├── affiliates/
    │       │   ├── RegisterAffiliateUseCaseImpl.java
    │       │   ├── GetAffiliateUseCaseImpl.java
    │       │   └── UpdateAffiliateUseCaseImpl.java
    │       └── creditapplications/
    │           ├── RegisterCreditApplicationUseCaseImpl.java
    │           ├── EvaluateCreditApplicationUseCaseImpl.java
    │           └── GetCreditApplicationUseCaseImpl.java
    │
    └── infrastructure/            # 🔌 CAPA DE INFRAESTRUCTURA
        ├── controllers/           # Adaptadores de entrada (REST API)
        │   ├── AuthController.java
        │   ├── AffiliateController.java
        │   ├── CreditApplicationController.java
        │   └── HomeController.java
        ├── adapters/              # Adaptadores de salida
        │   ├── UserRepositoryAdapter.java
        │   ├── AffiliateRepositoryAdapter.java
        │   ├── CreditApplicationRepositoryAdapter.java
        │   └── RiskCentralAdapter.java
        ├── repositories/          # Repositorios JPA
        │   ├── JpaUserRepository.java
        │   ├── JpaAffiliateRepository.java
        │   └── JpaCreditApplicationRepository.java
        ├── entities/              # Entidades JPA (persistencia)
        │   ├── UserEntity.java
        │   ├── AffiliateEntity.java
        │   └── CreditApplicationEntity.java
        ├── mappers/               # Mappers Domain <-> Entity <-> DTO
        │   ├── auth/UserMapper.java
        │   ├── affiliates/AffiliateMapper.java
        │   └── creditapplications/CreditApplicationMapper.java
        ├── config/                # Configuraciones
        │   ├── SecurityConfig.java
        │   ├── UseCaseConfig.java
        │   ├── OpenApiConfig.java
        │   └── DatabaseConfig.java
        ├── security/              # Seguridad JWT
        │   ├── JwtService.java
        │   └── JwtAuthenticationFilter.java
        └── web/                   # DTOs y manejo de respuestas
            ├── dto/
            ├── response/AppResponse.java
            └── advice/GlobalExceptionHandler.java
```

**Funcionalidades:**
- ✅ Autenticación y autorización JWT
- ✅ Gestión de usuarios (ADMIN, ANALISTA, AFILIADO)
- ✅ Gestión de afiliados
- ✅ Solicitudes de crédito (crear, evaluar, consultar)
- ✅ Comunicación con Risk Central para evaluación de riesgo
- ✅ Métricas con Prometheus/Actuator

---

### 2. Risk Central Mock Service (Puerto 8081)

**Propósito:** Servicio mock que simula un sistema central de evaluación de riesgo crediticio.

**Estructura:**

```
risk-central-mock-service/
└── src/main/java/com/coopcredit/risk_central_mock_service/
    ├── RiskCentralMockServiceApplication.java
    ├── domain/
    │   ├── model/RiskScore.java
    │   └── ports/
    ├── application/
    │   └── services/RiskEvaluationService.java
    └── infrastructure/
        ├── controllers/RiskEvaluationController.java
        ├── config/
        └── web/dto/
```

**Funcionalidades:**
- ✅ Endpoint para evaluación de riesgo
- ✅ Generación de score aleatorio (simulación)
- ✅ Retorna recomendación: APPROVED, REJECTED, REVIEW

---

## Dockerfiles

### Explicación del Multi-Stage Build

Los Dockerfiles utilizan **Multi-Stage Build**, un patrón que optimiza el tamaño de la imagen final.

```dockerfile
# ═══════════════════════════════════════════════════════════════
# ETAPA 1: BUILD (Imagen grande con JDK completo)
# ═══════════════════════════════════════════════════════════════
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copia archivos de Maven para cachear dependencias
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

# Descarga dependencias (se cachea si pom.xml no cambia)
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Copia código fuente
COPY src src

# Compila la aplicación (sin tests para acelerar)
RUN ./mvnw package -DskipTests

# ═══════════════════════════════════════════════════════════════
# ETAPA 2: RUNTIME (Imagen pequeña solo con JRE)
# ═══════════════════════════════════════════════════════════════
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia SOLO el JAR compilado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### ¿Por qué Multi-Stage Build?

| Aspecto | Sin Multi-Stage | Con Multi-Stage |
|---------|-----------------|-----------------|
| **Tamaño imagen** | ~800MB (incluye JDK, Maven, código fuente) | ~200MB (solo JRE + JAR) |
| **Seguridad** | Expone herramientas de build | Solo contiene lo necesario |
| **Capas Docker** | Muchas capas innecesarias | Imagen limpia y optimizada |

### Diferencias entre Dockerfiles

| Servicio | Puerto | Características especiales |
|----------|--------|---------------------------|
| **creddit-application-service** | 8080 | Define `SPRING_PROFILES_ACTIVE=prod` |
| **risk-central-mock-service** | 8081 | Incluye HEALTHCHECK |
| **Dockerfile raíz** | 8081 | Genérico con HEALTHCHECK |

---

## Render.yaml - Configuración de Despliegue

El archivo `render.yaml` es una **Blueprint de Infraestructura como Código (IaC)** que define todos los servicios a desplegar en Render.

```yaml
# ═══════════════════════════════════════════════════════════════
# BASE DE DATOS
# ═══════════════════════════════════════════════════════════════
databases:
  - name: coopcredit-db           # Nombre del servicio
    databaseName: coopcredit_db   # Nombre de la BD PostgreSQL
    user: coopcredit_user         # Usuario de la BD
    plan: free                    # Plan gratuito de Render

# ═══════════════════════════════════════════════════════════════
# SERVICIOS WEB
# ═══════════════════════════════════════════════════════════════
services:
  # ─────────────────────────────────────────────────────────────
  # SERVICIO 1: Risk Central Mock
  # ─────────────────────────────────────────────────────────────
  - type: web                           # Tipo de servicio web
    name: risk-central-mock-service
    runtime: docker                     # Usa Docker para build
    region: oregon                      # Región del servidor
    plan: free                          # Plan gratuito
    branch: main                        # Rama de Git
    dockerfilePath: ./Dockerfile        # Ruta al Dockerfile
    dockerContext: ./risk-central-mock-service  # Contexto de build
    envVars:
      - key: PORT
        value: 8081
      - key: SPRING_PROFILES_ACTIVE
        value: prod
    healthCheckPath: /actuator/health   # Endpoint de health check

  # ─────────────────────────────────────────────────────────────
  # SERVICIO 2: Creddit Application (depende del anterior)
  # ─────────────────────────────────────────────────────────────
  - type: web
    name: creddit-application-service
    runtime: docker
    region: oregon
    plan: free
    branch: main
    dockerfilePath: ./Dockerfile
    dockerContext: ./creddit-application-service
    envVars:
      - key: SERVER_PORT
        value: 8080
      # ═══ Variables desde la Base de Datos ═══
      - key: DB_USERNAME
        fromDatabase:
          name: coopcredit-db
          property: user              # Render inyecta automáticamente
      - key: DB_PASSWORD
        fromDatabase:
          name: coopcredit-db
          property: password
      - key: DB_HOST
        fromDatabase:
          name: coopcredit-db
          property: host
      # ═══ Variable desde otro Servicio ═══
      - key: RISK_CENTRAL_URL
        fromService:
          type: web
          name: risk-central-mock-service
          envVarKey: RENDER_EXTERNAL_URL  # URL pública del servicio
      # ═══ Variable generada automáticamente ═══
      - key: JWT_SECRET
        generateValue: true           # Render genera un valor seguro
```

### Características Clave del render.yaml:

1. **`fromDatabase`**: Inyecta credenciales de BD automáticamente
2. **`fromService`**: Conecta servicios entre sí usando URLs internas
3. **`generateValue`**: Genera secrets seguros automáticamente
4. **`healthCheckPath`**: Verifica que el servicio esté saludable
5. **Orden de despliegue**: Risk Central se despliega primero (creddit-application depende de él)

---

## Principios SOLID

### S - Single Responsibility Principle (Responsabilidad Única)

**Dónde se aplica:** En TODA la arquitectura

| Archivo | Responsabilidad Única |
|---------|----------------------|
| `RegisterUserUseCaseImpl.java` | Solo registra usuarios |
| `AuthenticateUserUseCaseImpl.java` | Solo autentica usuarios |
| `UserRepositoryAdapter.java` | Solo accede a datos de usuarios |
| `AuthController.java` | Solo maneja requests HTTP de auth |
| `JwtService.java` | Solo genera/valida tokens JWT |
| `UserMapper.java` | Solo convierte entre User/UserEntity |

**Ejemplo:**
```java
// RegisterUserUseCaseImpl.java - UNA sola responsabilidad
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {
    @Override
    public User execute(User user) {
        // Solo se encarga de registrar usuarios
        // NO autentica, NO genera tokens, NO envía emails
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ConflictException("User", "username", user.getUsername());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
```

**¿Por qué?** Si necesitas modificar cómo se registran usuarios, solo tocas este archivo. No afectas autenticación ni otros módulos.

---

### O - Open/Closed Principle (Abierto/Cerrado)

**Dónde se aplica:** Puertos e interfaces

**Ejemplo:** Los puertos de salida permiten agregar nuevas implementaciones sin modificar el dominio.

```java
// Puerto de salida - CERRADO para modificación
public interface RiskCentralPort {
    RiskEvaluationResult evaluateRisk(Long affiliateId, BigDecimal amount);
}

// Implementación actual - ABIERTO para extensión
@Component
public class RiskCentralAdapter implements RiskCentralPort {
    // Llama al servicio mock via HTTP
}

// NUEVA implementación (sin modificar el puerto ni el dominio)
@Component
@Profile("production")
public class RealRiskCentralAdapter implements RiskCentralPort {
    // Llama a un servicio real de evaluación de riesgo
}
```

**¿Por qué?** Puedes cambiar de servicio mock a servicio real sin modificar la lógica de negocio.

---

### L - Liskov Substitution Principle (Sustitución de Liskov)

**Dónde se aplica:** Implementaciones de interfaces

```java
// Cualquier implementación de UserRepositoryPort funciona igual
public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findByUsername(String username);
}

// Implementación JPA
public class UserRepositoryAdapter implements UserRepositoryPort { ... }

// Implementación en memoria (para tests)
public class InMemoryUserRepository implements UserRepositoryPort { ... }

// Ambas son intercambiables sin romper el sistema
```

**¿Por qué?** Los tests pueden usar `InMemoryUserRepository` y producción usa `UserRepositoryAdapter`, el código de negocio no nota la diferencia.

---

### I - Interface Segregation Principle (Segregación de Interfaces)

**Dónde se aplica:** Casos de uso separados

```java
// ❌ MAL: Una interfaz gigante
public interface CreditApplicationUseCase {
    CreditApplication register(...);
    CreditApplication evaluate(...);
    CreditApplication getById(...);
    List<CreditApplication> getPending();
    List<CreditApplication> getByAffiliate(...);
}

// ✅ BIEN: Interfaces pequeñas y específicas
public interface RegisterCreditApplicationUseCase {
    CreditApplication execute(CreditApplication application);
}

public interface EvaluateCreditApplicationUseCase {
    CreditApplication execute(Long applicationId);
}

public interface GetCreditApplicationUseCase {
    CreditApplication execute(Long id);
}
```

**¿Por qué?** El `CreditApplicationController` solo inyecta los casos de uso que necesita. Si solo consulta, no necesita inyectar `EvaluateCreditApplicationUseCase`.

---

### D - Dependency Inversion Principle (Inversión de Dependencias)

**Dónde se aplica:** TODO el sistema (es la BASE de la Arquitectura Hexagonal)

```
┌─────────────────────────────────────────────────────────────┐
│                    CAPA DE DOMINIO                          │
│  (Define interfaces/puertos - NO depende de nadie)          │
│                                                              │
│   UserRepositoryPort ◄─────────┐                            │
│   RiskCentralPort ◄────────────┤                            │
│   RegisterUserUseCase ◄────────┤                            │
└─────────────────────────────────┼────────────────────────────┘
                                  │
                    Las dependencias APUNTAN HACIA ADENTRO
                                  │
┌─────────────────────────────────┼────────────────────────────┐
│                    CAPA DE INFRAESTRUCTURA                   │
│  (Implementa las interfaces del dominio)                     │
│                                                              │
│   UserRepositoryAdapter ───────┘                             │
│   RiskCentralAdapter ──────────┘                             │
│   RegisterUserUseCaseImpl ─────┘                             │
└─────────────────────────────────────────────────────────────┘
```

**Ejemplo concreto:**
```java
// DOMINIO: Define QUÉ necesita (no CÓMO)
public interface UserRepositoryPort {
    User save(User user);
}

// APLICACIÓN: Usa la abstracción, NO la implementación
public class RegisterUserUseCaseImpl {
    private final UserRepositoryPort userRepository;  // ◄── Interfaz, NO clase concreta
    
    public RegisterUserUseCaseImpl(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }
}

// INFRAESTRUCTURA: Implementa la interfaz
@Component
public class UserRepositoryAdapter implements UserRepositoryPort {
    private final JpaUserRepository jpaRepository;
    
    @Override
    public User save(User user) {
        // Implementación con JPA
    }
}

// CONFIGURACIÓN: Conecta todo (Inyección de Dependencias)
@Configuration
public class UseCaseConfig {
    @Bean
    public RegisterUserUseCase registerUserUseCase(UserRepositoryPort userRepository) {
        return new RegisterUserUseCaseImpl(userRepository);  // Spring inyecta el Adapter
    }
}
```

**¿Por qué?** El dominio no conoce JPA, PostgreSQL ni Spring. Podrías cambiar a MongoDB sin tocar la lógica de negocio.

---

## Patrones de Diseño

### 1. Patrón Ports & Adapters (Hexagonal Architecture)

**Ubicación:** Estructura completa del proyecto

```
                    ┌─────────────────────────────────────┐
                    │           DOMINIO                   │
    Adaptadores     │  ┌─────────────────────────────┐   │     Adaptadores
    de Entrada      │  │      Lógica de Negocio      │   │     de Salida
                    │  │                              │   │
   ┌────────────┐   │  │   Casos de Uso              │   │   ┌────────────┐
   │ Controller │◄──┼──┤   (RegisterUserUseCase)     ├───┼──►│ Repository │
   │   (REST)   │   │  │                              │   │   │  Adapter   │
   └────────────┘   │  │   Modelos de Dominio        │   │   └────────────┘
                    │  │   (User, Affiliate)          │   │
                    │  │                              │   │   ┌────────────┐
                    │  │   Puertos (Interfaces)       ├───┼──►│   Risk     │
                    │  │                              │   │   │  Central   │
                    │  └─────────────────────────────┘   │   │  Adapter   │
                    │                                     │   └────────────┘
                    └─────────────────────────────────────┘
```

**Archivos involucrados:**
- **Puertos de entrada:** `RegisterUserUseCase.java`, `GetAffiliateUseCase.java`
- **Puertos de salida:** `UserRepositoryPort.java`, `RiskCentralPort.java`
- **Adaptadores de entrada:** `AuthController.java`, `AffiliateController.java`
- **Adaptadores de salida:** `UserRepositoryAdapter.java`, `RiskCentralAdapter.java`

---

### 2. Patrón Repository

**Ubicación:** `infrastructure/repositories/` y `infrastructure/adapters/`

```java
// Interfaz del patrón Repository (Puerto de salida)
public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}

// Implementación que oculta los detalles de JPA
@Component
public class UserRepositoryAdapter implements UserRepositoryPort {
    private final JpaUserRepository jpaRepository;
    private final UserMapper mapper;
    
    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        UserEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
```

**¿Por qué?** Abstrae el acceso a datos. El dominio trabaja con `User`, no con `UserEntity` ni JPA.

---

### 3. Patrón Mapper (Data Mapper)

**Ubicación:** `infrastructure/mappers/`

```java
@Component
public class UserMapper {
    
    // Convierte Entity (JPA) → Domain
    public User toDomain(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setUsername(entity.getUsername());
        // ... más campos
        return user;
    }
    
    // Convierte Domain → Entity (JPA)
    public UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        // ... más campos
        return entity;
    }
}
```

**¿Por qué?** Separa el modelo de dominio (`User`) del modelo de persistencia (`UserEntity`). Puedes cambiar la BD sin afectar el dominio.

---

### 4. Patrón DTO (Data Transfer Object)

**Ubicación:** `infrastructure/web/dto/`

```java
// Request DTO - Lo que recibe el API
public class RegisterRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String role;
}

// Response DTO - Lo que devuelve el API
public class AuthResponse {
    private String token;
    private String username;
    private String role;
}
```

**¿Por qué?** Controla exactamente qué datos entran/salen del API. No expone entidades internas.

---

### 5. Patrón Factory (via Spring Configuration)

**Ubicación:** `infrastructure/config/UseCaseConfig.java`

```java
@Configuration
public class UseCaseConfig {
    
    // Factory method para crear casos de uso
    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepositoryPort userRepository,
            PasswordEncoder passwordEncoder) {
        return new RegisterUserUseCaseImpl(userRepository, passwordEncoder);
    }
    
    @Bean
    public EvaluateCreditApplicationUseCase evaluateCreditApplicationUseCase(
            CreditApplicationRepositoryPort creditAppRepo,
            AffiliateRepositoryPort affiliateRepo,
            RiskEvaluationRepositoryPort riskRepo,
            RiskCentralPort riskCentral) {
        return new EvaluateCreditApplicationUseCaseImpl(
            creditAppRepo, affiliateRepo, riskRepo, riskCentral
        );
    }
}
```

**¿Por qué?** Centraliza la creación de objetos complejos. Los casos de uso no tienen anotaciones de Spring (`@Service`), manteniendo el dominio limpio.

---

### 6. Patrón Strategy (Implicit)

**Ubicación:** Puertos de salida con múltiples implementaciones posibles

```java
// Estrategia para evaluación de riesgo
public interface RiskCentralPort {
    RiskEvaluationResult evaluateRisk(Long affiliateId, BigDecimal amount);
}

// Estrategia 1: Servicio Mock (desarrollo)
@Component
@Profile("dev")
public class MockRiskCentralAdapter implements RiskCentralPort { }

// Estrategia 2: Servicio Real (producción)
@Component
@Profile("prod")
public class RealRiskCentralAdapter implements RiskCentralPort { }
```

---

### 7. Patrón Filter Chain

**Ubicación:** `infrastructure/security/`

```java
// Spring Security usa una cadena de filtros
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(...))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

---

### 8. Patrón Builder (Implícito con Lombok)

**Ubicación:** DTOs y Entidades

```java
@Getter
@Setter
@Builder  // Lombok genera el Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditApplication {
    private Long id;
    private Long affiliateId;
    private BigDecimal amount;
    private String status;
}

// Uso:
CreditApplication app = CreditApplication.builder()
    .affiliateId(1L)
    .amount(new BigDecimal("5000"))
    .status("PENDING")
    .build();
```

---

## Resumen Visual de Patrones y SOLID

```
┌────────────────────────────────────────────────────────────────────────────┐
│                        ARQUITECTURA HEXAGONAL                              │
│                                                                            │
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │                         INFRASTRUCTURE                               │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐               │  │
│  │  │ Controllers  │  │   Adapters   │  │    Config    │               │  │
│  │  │  (REST API)  │  │ (Repository) │  │  (Factory)   │               │  │
│  │  │              │  │              │  │              │               │  │
│  │  │ Patrón: DTO  │  │ Patrón:      │  │ Patrón:      │               │  │
│  │  │ SOLID: S     │  │ Repository   │  │ Factory      │               │  │
│  │  │              │  │ Mapper       │  │              │               │  │
│  │  │              │  │ SOLID: S,D   │  │ SOLID: D     │               │  │
│  │  └──────┬───────┘  └──────┬───────┘  └──────────────┘               │  │
│  └─────────┼─────────────────┼──────────────────────────────────────────┘  │
│            │                 │                                             │
│            ▼                 ▼                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │                           DOMAIN                                     │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐               │  │
│  │  │    Ports     │  │    Model     │  │  Exceptions  │               │  │
│  │  │  (in/out)    │  │   (POJO)     │  │              │               │  │
│  │  │              │  │              │  │              │               │  │
│  │  │ Patrón:      │  │ Patrón:      │  │              │               │  │
│  │  │ Hexagonal    │  │ Domain Model │  │              │               │  │
│  │  │ SOLID: I,D   │  │ SOLID: S     │  │              │               │  │
│  │  └──────────────┘  └──────────────┘  └──────────────┘               │  │
│  └─────────────────────────────────────────────────────────────────────┘  │
│            ▲                                                               │
│            │                                                               │
│  ┌─────────┴───────────────────────────────────────────────────────────┐  │
│  │                         APPLICATION                                  │  │
│  │  ┌──────────────────────────────────────────────────────────────┐   │  │
│  │  │                      Use Cases                                │   │  │
│  │  │  RegisterUserUseCaseImpl, EvaluateCreditApplicationUseCaseImpl│   │  │
│  │  │                                                               │   │  │
│  │  │  Patrones: Strategy (implícito)                               │   │  │
│  │  │  SOLID: S, O, L, I, D (TODOS)                                 │   │  │
│  │  └──────────────────────────────────────────────────────────────┘   │  │
│  └─────────────────────────────────────────────────────────────────────┘  │
│                                                                            │
└────────────────────────────────────────────────────────────────────────────┘
```

---

## Conclusión

El sistema CoopCredit implementa una **Arquitectura Hexagonal** robusta que:

1. **Aísla la lógica de negocio** del framework y la base de datos
2. **Facilita el testing** mediante inversión de dependencias
3. **Permite escalabilidad** agregando nuevos adaptadores sin modificar el dominio
4. **Cumple con SOLID** en todas las capas
5. **Usa patrones de diseño probados** para resolver problemas comunes

Esta arquitectura es ideal para sistemas que requieren mantenibilidad a largo plazo y equipos de desarrollo múltiples trabajando en paralelo.
