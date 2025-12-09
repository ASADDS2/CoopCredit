# Run Instructions

## Docker (recommended)

```bash
docker-compose up -d --build
```

Services:
- Creddit Application: http://localhost:8080
- Risk Central Mock: http://localhost:8081
- PostgreSQL: localhost:5433 (user: coopcredit_user / pass: coopcredit_pass)
- Prometheus: http://localhost:19090
- Grafana: http://localhost:3000 (admin/admin)

## Local Dev (backend only)

```bash
cd creddit-application-service
./mvnw spring-boot:run
```

Configure DB in `src/main/resources/application.yaml` or use environment variables:

- `DB_USERNAME`, `DB_PASSWORD`, `SPRING_DATASOURCE_URL`
- `JWT_SECRET`, `SERVER_PORT`, `RISK_CENTRAL_URL`, `CORS_ORIGINS`

## Health and Docs

- Health: `GET /actuator/health`
- Metrics: `GET /actuator/prometheus`
- Swagger UI: `GET /swagger-ui.html`

## Prometheus scrape

Prometheus is preconfigured to scrape both services at `/actuator/prometheus`.
