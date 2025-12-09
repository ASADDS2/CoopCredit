package com.coopcredit.creddit_application_service.infrastructure.repositories;

import com.coopcredit.creddit_application_service.infrastructure.entities.RiskEvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaRiskEvaluationRepository extends JpaRepository<RiskEvaluationEntity, Long> {

    Optional<RiskEvaluationEntity> findByCreditApplicationId(Long creditApplicationId);
}
