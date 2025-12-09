package com.coopcredit.creddit_application_service.infrastructure.mappers.creditapplications;

import com.coopcredit.creddit_application_service.domain.model.RiskEvaluation;
import com.coopcredit.creddit_application_service.infrastructure.entities.RiskEvaluationEntity;
import org.springframework.stereotype.Component;

@Component
public class RiskEvaluationMapper {

    public RiskEvaluation toDomain(RiskEvaluationEntity entity) {
        if (entity == null) {
            return null;
        }

        return new RiskEvaluation(
                entity.getId(),
                entity.getCreditApplication() != null ? entity.getCreditApplication().getId() : null,
                entity.getDocument(),
                entity.getScore(),
                entity.getRiskLevel(),
                entity.getDetail(),
                entity.getEvaluatedAt());
    }

    public RiskEvaluationEntity toEntity(RiskEvaluation domain) {
        if (domain == null) {
            return null;
        }

        RiskEvaluationEntity entity = new RiskEvaluationEntity();
        entity.setId(domain.getId());
        // Note: creditApplication relationship will be set in the adapter
        entity.setDocument(domain.getDocument());
        entity.setScore(domain.getScore());
        entity.setRiskLevel(domain.getRiskLevel());
        entity.setDetail(domain.getDetail());
        entity.setEvaluatedAt(domain.getEvaluatedAt());
        return entity;
    }
}
