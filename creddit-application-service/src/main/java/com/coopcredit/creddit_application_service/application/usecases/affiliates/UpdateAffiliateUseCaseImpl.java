package com.coopcredit.creddit_application_service.application.usecases.affiliates;

import com.coopcredit.creddit_application_service.domain.exception.NotFoundException;
import com.coopcredit.creddit_application_service.domain.model.Affiliate;
import com.coopcredit.creddit_application_service.domain.ports.in.affiliates.UpdateAffiliateUseCase;
import com.coopcredit.creddit_application_service.domain.ports.out.AffiliateRepositoryPort;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UpdateAffiliateUseCaseImpl implements UpdateAffiliateUseCase {

    private final AffiliateRepositoryPort affiliateRepository;

    public UpdateAffiliateUseCaseImpl(AffiliateRepositoryPort affiliateRepository) {
        this.affiliateRepository = affiliateRepository;
    }

    @Override
    public Affiliate execute(Long id, Affiliate updatedAffiliate) {
        // Get existing affiliate
        Affiliate existingAffiliate = affiliateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Affiliate", id));

        // Validation: salary must be greater than 0 if provided
        if (updatedAffiliate.getSalary() != null &&
                updatedAffiliate.getSalary().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Salary must be greater than 0");
        }

        // Update fields
        if (updatedAffiliate.getName() != null) {
            existingAffiliate.setName(updatedAffiliate.getName());
        }
        if (updatedAffiliate.getSalary() != null) {
            existingAffiliate.setSalary(updatedAffiliate.getSalary());
        }
        if (updatedAffiliate.getStatus() != null) {
            existingAffiliate.setStatus(updatedAffiliate.getStatus());
        }
        if (updatedAffiliate.getAffiliationDate() != null) {
            existingAffiliate.setAffiliationDate(updatedAffiliate.getAffiliationDate());
        }

        // Update timestamp
        existingAffiliate.setUpdatedAt(LocalDateTime.now());

        // Save and return
        return affiliateRepository.save(existingAffiliate);
    }
}
