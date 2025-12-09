package com.coopcredit.creddit_application_service.application.usecases.creditapplications;

import com.coopcredit.creddit_application_service.domain.exception.AffiliateNotActiveException;
import com.coopcredit.creddit_application_service.domain.exception.BusinessRuleException;
import com.coopcredit.creddit_application_service.domain.exception.NotFoundException;
import com.coopcredit.creddit_application_service.domain.model.Affiliate;
import com.coopcredit.creddit_application_service.domain.model.CreditApplication;
import com.coopcredit.creddit_application_service.domain.ports.in.creditapplications.RegisterCreditApplicationUseCase;
import com.coopcredit.creddit_application_service.domain.ports.out.AffiliateRepositoryPort;
import com.coopcredit.creddit_application_service.domain.ports.out.CreditApplicationRepositoryPort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class RegisterCreditApplicationUseCaseImpl implements RegisterCreditApplicationUseCase {

    private final CreditApplicationRepositoryPort creditApplicationRepository;
    private final AffiliateRepositoryPort affiliateRepository;

    private static final int MINIMUM_AFFILIATION_MONTHS = 6;

    public RegisterCreditApplicationUseCaseImpl(
            CreditApplicationRepositoryPort creditApplicationRepository,
            AffiliateRepositoryPort affiliateRepository) {
        this.creditApplicationRepository = creditApplicationRepository;
        this.affiliateRepository = affiliateRepository;
    }

    @Override
    public CreditApplication execute(CreditApplication creditApplication) {
        // Validation: Get affiliate
        Affiliate affiliate = affiliateRepository.findById(creditApplication.getAffiliateId())
                .orElseThrow(() -> new NotFoundException("Affiliate", creditApplication.getAffiliateId()));

        // Validation: Affiliate must be ACTIVE
        if (!"ACTIVE".equals(affiliate.getStatus())) {
            throw new AffiliateNotActiveException(affiliate.getId());
        }

        // Validation: Minimum affiliation time
        long monthsSinceAffiliation = ChronoUnit.MONTHS.between(
                affiliate.getAffiliationDate(),
                LocalDate.now());

        if (monthsSinceAffiliation < MINIMUM_AFFILIATION_MONTHS) {
            throw new BusinessRuleException(
                    String.format(
                            "Affiliate must have at least %d months of affiliation to apply for credit. Current: %d months",
                            MINIMUM_AFFILIATION_MONTHS,
                            monthsSinceAffiliation));
        }

        // Validation: Requested amount must not exceed 10x salary
        BigDecimal maximumAllowedAmount = affiliate.getSalary().multiply(BigDecimal.valueOf(10));
        if (creditApplication.getRequestedAmount().compareTo(maximumAllowedAmount) > 0) {
            throw new BusinessRuleException(
                    String.format("Requested amount %s exceeds maximum allowed %s (10x salary of %s)",
                            creditApplication.getRequestedAmount(),
                            maximumAllowedAmount,
                            affiliate.getSalary()));
        }

        // Set default values
        creditApplication.setStatus("PENDING");
        creditApplication.setApplicationDate(LocalDate.now());

        LocalDateTime now = LocalDateTime.now();
        creditApplication.setCreatedAt(now);
        creditApplication.setUpdatedAt(now);

        // Save and return
        return creditApplicationRepository.save(creditApplication);
    }
}
