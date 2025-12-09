package com.coopcredit.risk_central_mock_service.application.usecases;

import org.springframework.stereotype.Service;

import com.coopcredit.risk_central_mock_service.domain.model.RiskEvaluation;
import com.coopcredit.risk_central_mock_service.domain.model.RiskLevel;
import com.coopcredit.risk_central_mock_service.domain.ports.in.CalculateRiskUseCase;

/**
 * Implementation of risk calculation use case
 * Uses document hash to generate consistent, deterministic scores
 */
@Service
public class CalculateRiskUseCaseImpl implements CalculateRiskUseCase {

    private static final int MIN_SCORE = 300;
    private static final int MAX_SCORE = 950;
    private static final int SCORE_RANGE = MAX_SCORE - MIN_SCORE;

    @Override
    public RiskEvaluation execute(String documentId, double amount, int term) {
        // Generate consistent score based on document hash
        int score = calculateScore(documentId);

        // Determine risk level based on score
        RiskLevel riskLevel = RiskLevel.fromScore(score);

        // Get detail message
        String details = riskLevel.getDetail();

        return new RiskEvaluation(documentId, score, riskLevel, details);
    }

    /**
     * Calculates a deterministic score based on document hash
     * Same document will always produce the same score
     * 
     * @param documentId Document number
     * @return Score between 300 and 950
     */
    private int calculateScore(String documentId) {
        if (documentId == null || documentId.isEmpty()) {
            return MIN_SCORE;
        }

        // Generate hash using Java's hashCode
        int hash = documentId.hashCode();

        // Convert to positive seed and normalize to range [0, 1000)
        int seed = Math.abs(hash % 1000);

        // Map seed to score range [300, 950]
        // Using modulo to ensure consistent distribution
        int score = MIN_SCORE + (seed * SCORE_RANGE / 1000);

        // Ensure score is within valid range
        return Math.max(MIN_SCORE, Math.min(MAX_SCORE, score));
    }
}
