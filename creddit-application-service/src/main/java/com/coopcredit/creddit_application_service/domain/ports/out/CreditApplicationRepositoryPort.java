package com.coopcredit.creddit_application_service.domain.ports.out;

import com.coopcredit.creddit_application_service.domain.model.CreditApplication;

import java.util.List;
import java.util.Optional;

public interface CreditApplicationRepositoryPort {

    CreditApplication save(CreditApplication creditApplication);

    Optional<CreditApplication> findById(Long id);

    List<CreditApplication> findAll();

    List<CreditApplication> findByAffiliateId(Long affiliateId);

    List<CreditApplication> findByStatus(String status);

    void deleteById(Long id);
}
