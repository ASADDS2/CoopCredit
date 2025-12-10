# Diagrama de Casos de Uso - CoopCredit

## Diagrama Principal

```mermaid
graph LR
    subgraph "Actores"
        AFILIADO[👤 Afiliado]
        ANALISTA[👔 Analista]
        ADMIN[👨‍💼 Admin]
        SYSTEM[🤖 Sistema]
    end
    
    subgraph "Casos de Uso - Gestión de Afiliados"
        UC1[Registrar Afiliado]
        UC2[Consultar Afiliado]
        UC3[Actualizar Afiliado]
        UC4[Listar Afiliados]
    end
    
    subgraph "Casos de Uso - Gestión de Créditos"
        UC5[Solicitar Crédito]
        UC6[Consultar Solicitud]
        UC7[Listar Mis Solicitudes]
        UC8[Evaluar Solicitud]
        UC9[Listar Solicitudes Pendientes]
        UC10[Aprobar/Rechazar Solicitud]
    end
    
    subgraph "Casos de Uso - Autenticación"
        UC11[Registrar Usuario]
        UC12[Iniciar Sesión]
        UC13[Cerrar Sesión]
    end
    
    subgraph "Casos de Uso - Sistema"
        UC14[Evaluar Riesgo Automático]
        UC15[Calcular Cuota]
        UC16[Validar Documentos]
    end
    
    %% Relaciones Admin
    ADMIN --> UC1
    ADMIN --> UC2
    ADMIN --> UC3
    ADMIN --> UC4
    ADMIN --> UC10
    ADMIN --> UC11
    
    %% Relaciones Analista
    ANALISTA --> UC2
    ANALISTA --> UC8
    ANALISTA --> UC9
    ANALISTA --> UC10
    
    %% Relaciones Afiliado
    AFILIADO --> UC5
    AFILIADO --> UC6
    AFILIADO --> UC7
    AFILIADO --> UC12
    AFILIADO --> UC13
    
    %% Relaciones Sistema
    UC5 --> UC14
    UC8 --> UC14
    UC5 --> UC15
    UC1 --> UC16
    
    %% Includes
    UC5 -.include.-> UC15
    UC8 -.include.-> UC14
    
    %% Extends
    UC10 -.extends.-> UC8
```

## Descripción de Casos de Uso

### Gestión de Afiliados

#### UC1: Registrar Afiliado
- **Actor Principal**: Admin
- **Precondiciones**: Usuario autenticado con rol ADMIN
- **Flujo Principal**:
  1. Admin ingresa datos del afiliado
  2. Sistema valida documento único
  3. Sistema valida datos requeridos
  4. Sistema crea afiliado con estado ACTIVO
  5. Sistema retorna confirmación

#### UC2: Consultar Afiliado
- **Actores**: Admin, Analista
- **Precondiciones**: Usuario autenticado
- **Flujo Principal**:
  1. Usuario busca por documento o ID
  2. Sistema retorna datos del afiliado

### Gestión de Créditos

#### UC5: Solicitar Crédito
- **Actor Principal**: Afiliado
- **Precondiciones**: 
  - Afiliado autenticado
  - Afiliado con estado ACTIVO
  - Antigüedad mínima 6 meses
- **Flujo Principal**:
  1. Afiliado ingresa monto, plazo y propósito
  2. Sistema valida monto máximo según salario
  3. Sistema calcula cuota mensual
  4. Sistema valida relación cuota/ingreso < 40%
  5. Sistema invoca evaluación de riesgo automática
  6. Sistema crea solicitud con estado PENDING
  7. Sistema retorna número de solicitud

#### UC8: Evaluar Solicitud
- **Actor Principal**: Analista
- **Precondiciones**: 
  - Usuario autenticado con rol ANALISTA
  - Solicitud en estado PENDING
- **Flujo Principal**:
  1. Analista revisa solicitud y evaluación de riesgo
  2. Analista puede solicitar re-evaluación
  3. Sistema actualiza evaluación de riesgo
  4. Analista toma decisión

#### UC14: Evaluar Riesgo Automático
- **Actor**: Sistema
- **Trigger**: Nueva solicitud o re-evaluación
- **Flujo**:
  1. Sistema envía datos a Risk Central
  2. Risk Central calcula score (0-1000)
  3. Risk Central determina nivel de riesgo
  4. Sistema almacena evaluación
  5. Sistema actualiza estado de solicitud

### Autenticación y Seguridad

#### UC11: Registrar Usuario
- **Actor Principal**: Admin
- **Flujo Principal**:
  1. Admin crea usuario con rol específico
  2. Sistema valida username único
  3. Sistema hashea password
  4. Sistema asocia con afiliado si aplica

#### UC12: Iniciar Sesión
- **Actores**: Todos
- **Flujo Principal**:
  1. Usuario ingresa credenciales
  2. Sistema valida credenciales
  3. Sistema genera JWT token
  4. Sistema retorna token y rol

## Matriz de Permisos por Rol

| Caso de Uso | AFILIADO | ANALISTA | ADMIN |
|-------------|----------|----------|--------|
| Registrar Afiliado | ❌ | ❌ | ✅ |
| Consultar Afiliado | ❌ | ✅ | ✅ |
| Actualizar Afiliado | ❌ | ❌ | ✅ |
| Listar Afiliados | ❌ | ❌ | ✅ |
| Solicitar Crédito | ✅ | ❌ | ❌ |
| Consultar Solicitud | ✅* | ✅ | ✅ |
| Listar Mis Solicitudes | ✅ | ❌ | ❌ |
| Evaluar Solicitud | ❌ | ✅ | ✅ |
| Listar Solicitudes Pendientes | ❌ | ✅ | ✅ |
| Aprobar/Rechazar | ❌ | ✅ | ✅ |

*Solo sus propias solicitudes

## Reglas de Negocio Principales

1. **Validación de Afiliado**:
   - Documento único en el sistema
   - Salario > 0
   - Estado debe ser ACTIVO para solicitar crédito

2. **Validación de Crédito**:
   - Monto máximo = Salario × 10
   - Cuota mensual < 40% del salario
   - Antigüedad mínima: 6 meses
   - Plazo: 6-60 meses

3. **Evaluación de Riesgo**:
   - Score 0-400: ALTO riesgo → Rechazo automático
   - Score 401-700: MEDIO riesgo → Revisión manual
   - Score 701-1000: BAJO riesgo → Pre-aprobado

4. **Seguridad**:
   - JWT con expiración 24 horas
   - Passwords hasheados con BCrypt
   - Validación de roles en cada endpoint
