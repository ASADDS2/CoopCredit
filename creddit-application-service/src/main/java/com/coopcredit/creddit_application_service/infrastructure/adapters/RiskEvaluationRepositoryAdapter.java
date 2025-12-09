package com.coopcredit.creddit_application_service.infrastructure.adapters;

import com.coopcredit.creddit_application_service.domain.model.RiskEvaluation;
import com.coopcredit.creddit_application_service.domain.ports.out.RiskEvaluationRepositoryPort;
import com.coopcredit.creddit_application_service.infrastructure.entities.CreditApplicationEntity;
import com.coopcredit.creddit_application_service.infrastructure.entities.RiskEvaluationEntity;
import com.coopcredit.creddit_application_service.infrastructure.mappers.creditapplications.RiskEvaluationMapper;
import com.coopcredit.creddit_application_service.infrastructure.repositories.JpaCreditApplicationRepository;
import com.coopcredit.creddit_application_service.infrastructure.repositories.JpaRiskEvaluationRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RiskEvaluationRepositoryAdapter implements RiskEvaluationRepositoryPort {

    private final JpaRiskEvaluationRepository jpaRepository;
    private final JpaCreditApplicationRepository creditApplicationRepository;
    private final RiskEvaluationMapper mapper;

    public RiskEvaluationRepositoryAdapter(
            JpaRiskEvaluationRepository jpaRepository,
            JpaCreditApplicationRepository creditApplicationRepository,
            RiskEvaluationMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.creditApplicationRepository = creditApplicationRepository;
        this.mapper = mapper;
    }

    @Override
    public RiskEvaluation save(RiskEvaluation riskEvaluation) {
        RiskEvaluationEntity entity = mapper.toEntity(riskEvaluation);

        // Set credit application relationship
        if (riskEvaluation.getCreditApplicationId() != null) {
            CreditApplicationEntity creditApplication = creditApplicationRepository
                    .findById(riskEvaluation.getCreditApplicationId())
                    .orElseThrow(() -> new RuntimeException("CreditApplication not found"));
            entity.setCreditApplication(creditApplication);
        }

        RiskEvaluationEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<RiskEvaluation> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<RiskEvaluation> findByCreditApplicationId(Long creditApplicationId) {
        return jpaRepository.findByCreditApplicationId(creditApplicationId)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
