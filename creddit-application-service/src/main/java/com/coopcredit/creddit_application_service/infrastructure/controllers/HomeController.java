package com.coopcredit.creddit_application_service.infrastructure.controllers;

import com.coopcredit.creddit_application_service.infrastructure.web.response.AppResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Home", description = "Welcome and general information endpoints")
public class HomeController {

    @GetMapping("/")
    @Operation(summary = "Welcome endpoint", description = "Returns welcome message and basic API information")
    public ResponseEntity<AppResponse<Map<String, Object>>> welcome() {
        Map<String, Object> welcomeData = new HashMap<>();
        welcomeData.put("message", "Welcome to CoopCredit Credit Application Service API");
        welcomeData.put("service", "creddit-application-service");
        welcomeData.put("version", "1.0.0");
        welcomeData.put("status", "running");
        welcomeData.put("timestamp", LocalDateTime.now());
        welcomeData.put("description", "Credit application management system for CoopCredit cooperative");
        welcomeData.put("architecture", "Hexagonal Architecture with Spring Boot");
        
        Map<String, Object> features = new HashMap<>();
        features.put("authentication", "JWT-based authentication");
        features.put("authorization", "Role-based access control (ADMIN, ANALISTA, AFILIADO)");
        features.put("documentation", "OpenAPI 3.0 with Swagger UI");
        features.put("monitoring", "Spring Boot Actuator with Prometheus metrics");
        features.put("resilience", "Circuit breaker pattern with Resilience4j");
        features.put("database", "PostgreSQL with JPA/Hibernate");
        
        welcomeData.put("features", features);
        
        Map<String, Object> endpoints = new HashMap<>();
        endpoints.put("authentication", Map.of(
            "login", "POST /api/auth/login",
            "register", "POST /api/auth/register"
        ));
        endpoints.put("credit_applications", Map.of(
            "submit", "POST /api/credit-applications",
            "get_by_id", "GET /api/credit-applications/{id}",
            "get_by_affiliate", "GET /api/credit-applications/affiliate/{affiliateId}",
            "get_pending", "GET /api/credit-applications/pending",
            "evaluate", "POST /api/credit-applications/{id}/evaluate"
        ));
        endpoints.put("affiliates", Map.of(
            "get_all", "GET /api/affiliates",
            "get_by_id", "GET /api/affiliates/{id}",
            "create", "POST /api/affiliates",
            "update", "PUT /api/affiliates/{id}"
        ));
        
        welcomeData.put("endpoints", endpoints);
        welcomeData.put("links", Map.of(
            "swagger_ui", "/swagger-ui.html",
            "openapi_json", "/v3/api-docs",
            "health_check", "/actuator/health",
            "metrics", "/actuator/metrics",
            "prometheus", "/actuator/prometheus"
        ));

        return ResponseEntity.ok(AppResponse.success("API is running successfully", welcomeData));
    }
}
