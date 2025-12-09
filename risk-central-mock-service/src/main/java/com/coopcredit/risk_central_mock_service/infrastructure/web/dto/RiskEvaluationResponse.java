package com.coopcredit.risk_central_mock_service.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for risk evaluation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskEvaluationResponse {
    private String documentId;
    private int score;
    private String riskLevel;
    private String details;

    /**
     * Creates response from domain model
     */
    public static RiskEvaluationResponse fromDomain(
            com.coopcredit.risk_central_mock_service.domain.model.RiskEvaluation evaluation) {
        return RiskEvaluationResponse.builder()
                .documentId(evaluation.getDocumentId())
                .score(evaluation.getScore())
                .riskLevel(evaluation.getRiskLevel().name())
                .details(evaluation.getDetails())
                .build();
    }
}
