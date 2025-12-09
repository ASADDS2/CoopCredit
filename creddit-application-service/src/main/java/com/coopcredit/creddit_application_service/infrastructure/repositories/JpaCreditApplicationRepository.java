package com.coopcredit.creddit_application_service.infrastructure.repositories;

import com.coopcredit.creddit_application_service.infrastructure.entities.CreditApplicationEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaCreditApplicationRepository extends JpaRepository<CreditApplicationEntity, Long> {

    @EntityGraph(attributePaths = { "affiliate", "riskEvaluation" })
    Optional<CreditApplicationEntity> findById(Long id);

    @Query("SELECT ca FROM CreditApplicationEntity ca JOIN FETCH ca.affiliate WHERE ca.affiliate.id = :affiliateId")
    List<CreditApplicationEntity> findByAffiliateId(Long affiliateId);

    @Query("SELECT ca FROM CreditApplicationEntity ca JOIN FETCH ca.affiliate WHERE ca.status = :status")
    List<CreditApplicationEntity> findByStatus(String status);
}
