package com.coopcredit.risk_central_mock_service.domain.model;

/**
 * Domain model representing a risk evaluation result
 * Pure POJO without framework dependencies
 */
public class RiskEvaluation {
    private final String documentId;
    private final int score;
    private final RiskLevel riskLevel;
    private final String details;

    public RiskEvaluation(String documentId, int score, RiskLevel riskLevel, String details) {
        this.documentId = documentId;
        this.score = score;
        this.riskLevel = riskLevel;
        this.details = details;
    }

    public String getDocumentId() {
        return documentId;
    }

    public int getScore() {
        return score;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public String getDetails() {
        return details;
    }
}
