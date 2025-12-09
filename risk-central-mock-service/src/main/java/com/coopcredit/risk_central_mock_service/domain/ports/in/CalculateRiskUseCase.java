package com.coopcredit.risk_central_mock_service.domain.ports.in;

import com.coopcredit.risk_central_mock_service.domain.model.RiskEvaluation;

/**
 * Input port for calculating risk evaluation
 */
public interface CalculateRiskUseCase {
    /**
     * Calculates risk evaluation for a given document
     * 
     * @param documentId Document number
     * @param amount     Requested amount
     * @param term       Term in months
     * @return Risk evaluation result
     */
    RiskEvaluation execute(String documentId, double amount, int term);
}
