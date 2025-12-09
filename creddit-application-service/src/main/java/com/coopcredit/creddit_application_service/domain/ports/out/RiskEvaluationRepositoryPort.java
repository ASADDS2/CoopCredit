package com.coopcredit.creddit_application_service.domain.ports.out;

import com.coopcredit.creddit_application_service.domain.model.RiskEvaluation;

import java.util.Optional;

public interface RiskEvaluationRepositoryPort {

    RiskEvaluation save(RiskEvaluation riskEvaluation);

    Optional<RiskEvaluation> findById(Long id);

    Optional<RiskEvaluation> findByCreditApplicationId(Long creditApplicationId);

    void deleteById(Long id);
}
