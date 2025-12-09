package com.coopcredit.risk_central_mock_service.domain.model;

/**
 * Risk level classification enum
 */
public enum RiskLevel {
    HIGH("HIGH"),
    MEDIUM("MEDIUM"),
    LOW("LOW");

    private final String description;

    RiskLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Determines risk level based on score
     * 
     * @param score Credit score between 300 and 950
     * @return Risk level classification
     */
    public static RiskLevel fromScore(int score) {
        if (score >= 701) {
            return LOW;
        } else if (score >= 501) {
            return MEDIUM;
        } else {
            return HIGH;
        }
    }

    /**
     * Gets detail message based on risk level
     * 
     * @return Detail message
     */
    public String getDetail() {
        return switch (this) {
            case HIGH -> "Limited credit history or high default risk.";
            case MEDIUM -> "Moderate credit history.";
            case LOW -> "Good credit history and low default risk.";
        };
    }
}
