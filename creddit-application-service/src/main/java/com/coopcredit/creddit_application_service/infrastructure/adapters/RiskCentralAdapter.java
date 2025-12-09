package com.coopcredit.creddit_application_service.infrastructure.adapters;

import com.coopcredit.creddit_application_service.domain.model.RiskEvaluation;
import com.coopcredit.creddit_application_service.domain.ports.out.RiskCentralPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class RiskCentralAdapter implements RiskCentralPort {

    private static final Logger logger = LoggerFactory.getLogger(RiskCentralAdapter.class);

    private final WebClient webClient;
    private final String riskCentralUrl;

    public RiskCentralAdapter(
            WebClient.Builder webClientBuilder,
            @Value("${risk-central.url:http://localhost:8081}") String riskCentralUrl) {
        this.riskCentralUrl = riskCentralUrl;
        this.webClient = webClientBuilder
                .baseUrl(riskCentralUrl)
                .build();
    }

    @Override
    @CircuitBreaker(name = "riskCentral", fallbackMethod = "evaluateRiskFallback")
    @Retry(name = "riskCentral")
    public RiskEvaluation evaluateRisk(String document, BigDecimal amount, Integer termMonths) {
        logger.info("Calling risk-central-service for document: {}", document);

        RiskEvaluationRequest request = new RiskEvaluationRequest(document, amount, termMonths);

        RiskEvaluationResponse response = webClient.post()
                .uri("/risk-evaluation")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(RiskEvaluationResponse.class)
                .timeout(Duration.ofSeconds(5))
                .block();

        if (response == null) {
            throw new RuntimeException("Empty response from risk-central-service");
        }

        logger.info("Risk evaluation received - Score: {}, Level: {}", response.score(), response.riskLevel());

        return new RiskEvaluation(
                null, // ID will be set when saved
                null, // creditApplicationId will be set later
                response.documentId(),
                response.score(),
                response.riskLevel(),
                response.details(),
                LocalDateTime.now());
    }

    /**
     * Fallback method when risk-central service is unavailable
     */
    public RiskEvaluation evaluateRiskFallback(String document, BigDecimal amount, Integer termMonths, Exception ex) {
        logger.error("Risk-central service unavailable. Using fallback. Error: {}", ex.getMessage());

        // Return conservative (high risk) evaluation when service is down
        return new RiskEvaluation(
                null,
                null,
                document,
                400, // Low score
                "HIGH", // High risk
                "Risk evaluation service unavailable - Fallback applied",
                LocalDateTime.now());
    }

    // DTOs for external service communication
    private record RiskEvaluationRequest(String documentId, BigDecimal amount, Integer term) {
    }

    private record RiskEvaluationResponse(String documentId, Integer score, String riskLevel, String details) {
    }
}
