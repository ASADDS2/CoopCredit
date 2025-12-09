package com.coopcredit.creddit_application_service.domain.ports.out;

import com.coopcredit.creddit_application_service.domain.model.RiskEvaluation;

import java.math.BigDecimal;

public interface RiskCentralPort {

    /**
     * Evaluates credit risk for an affiliate
     * 
     * @param document   Affiliate's document number
     * @param amount     Requested credit amount
     * @param termMonths Credit term in months
     * @return RiskEvaluation with score and risk level
     */
    RiskEvaluation evaluateRisk(String document, BigDecimal amount, Integer termMonths);
}
