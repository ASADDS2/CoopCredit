package com.coopcredit.creddit_application_service.application.usecases.affiliates;

import com.coopcredit.creddit_application_service.domain.exception.ConflictException;
import com.coopcredit.creddit_application_service.domain.model.Affiliate;
import com.coopcredit.creddit_application_service.domain.ports.in.affiliates.RegisterAffiliateUseCase;
import com.coopcredit.creddit_application_service.domain.ports.out.AffiliateRepositoryPort;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RegisterAffiliateUseCaseImpl implements RegisterAffiliateUseCase {

    private final AffiliateRepositoryPort affiliateRepository;

    public RegisterAffiliateUseCaseImpl(AffiliateRepositoryPort affiliateRepository) {
        this.affiliateRepository = affiliateRepository;
    }

    @Override
    public Affiliate execute(Affiliate affiliate) {
        // Validation: document must be unique
        if (affiliateRepository.existsByDocument(affiliate.getDocument())) {
            throw new ConflictException("Affiliate", "document", affiliate.getDocument());
        }

        // Validation: salary must be greater than 0
        if (affiliate.getSalary() == null || affiliate.getSalary().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Salary must be greater than 0");
        }

        // Set default status if not provided
        if (affiliate.getStatus() == null || affiliate.getStatus().isEmpty()) {
            affiliate.setStatus("ACTIVE");
        }

        // Set timestamps
        LocalDateTime now = LocalDateTime.now();
        affiliate.setCreatedAt(now);
        affiliate.setUpdatedAt(now);

        // Save and return
        return affiliateRepository.save(affiliate);
    }
}
