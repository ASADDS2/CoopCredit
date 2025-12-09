package com.coopcredit.creddit_application_service.application.usecases.creditapplications;


import com.coopcredit.creddit_application_service.domain.exception.NotFoundException;
import com.coopcredit.creddit_application_service.domain.model.Affiliate;
import com.coopcredit.creddit_application_service.domain.model.CreditApplication;
import com.coopcredit.creddit_application_service.domain.model.RiskEvaluation;
import com.coopcredit.creddit_application_service.domain.ports.in.creditapplications.EvaluateCreditApplicationUseCase;
import com.coopcredit.creddit_application_service.domain.ports.out.AffiliateRepositoryPort;
import com.coopcredit.creddit_application_service.domain.ports.out.CreditApplicationRepositoryPort;
import com.coopcredit.creddit_application_service.domain.ports.out.RiskCentralPort;
import com.coopcredit.creddit_application_service.domain.ports.out.RiskEvaluationRepositoryPort;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class EvaluateCreditApplicationUseCaseImpl implements EvaluateCreditApplicationUseCase {

    private final CreditApplicationRepositoryPort creditApplicationRepository;
    private final AffiliateRepositoryPort affiliateRepository;
    private final RiskEvaluationRepositoryPort riskEvaluationRepository;
    private final RiskCentralPort riskCentralPort;

    // Business rule constants
    private static final BigDecimal MAX_INSTALLMENT_INCOME_RATIO = BigDecimal.valueOf(40); // 40%
    private static final int MINIMUM_AFFILIATION_MONTHS = 6;
    private static final BigDecimal MAXIMUM_AMOUNT_MULTIPLIER = BigDecimal.valueOf(10); // 10x salary

    public EvaluateCreditApplicationUseCaseImpl(
            CreditApplicationRepositoryPort creditApplicationRepository,
            AffiliateRepositoryPort affiliateRepository,
            RiskEvaluationRepositoryPort riskEvaluationRepository,
            RiskCentralPort riskCentralPort) {
        this.creditApplicationRepository = creditApplicationRepository;
        this.affiliateRepository = affiliateRepository;
        this.riskEvaluationRepository = riskEvaluationRepository;
        this.riskCentralPort = riskCentralPort;
    }

    @Override
    public CreditApplication execute(Long creditApplicationId) {
        // Step 1: Get credit application
        CreditApplication application = creditApplicationRepository.findById(creditApplicationId)
                .orElseThrow(() -> new NotFoundException("CreditApplication", creditApplicationId));

        // Step 2: Get affiliate
        Affiliate affiliate = affiliateRepository.findById(application.getAffiliateId())
                .orElseThrow(() -> new NotFoundException("Affiliate", application.getAffiliateId()));

        // Step 3: Verify affiliate is ACTIVE
        if (!"ACTIVE".equals(affiliate.getStatus())) {
            application.setStatus("REJECTED");
            application.setRejectionReason("Affiliate is not active");
            application.setUpdatedAt(LocalDateTime.now());
            return creditApplicationRepository.save(application);
        }

        // Step 4: Call RiskCentralPort to get risk evaluation
        RiskEvaluation riskEvaluation = riskCentralPort.evaluateRisk(
                affiliate.getDocument(),
                application.getRequestedAmount(),
                application.getTermMonths());

        // Step 5: Apply internal policies
        String rejectionReason = evaluateInternalPolicies(application, affiliate, riskEvaluation);

        // Step 6: Save risk evaluation
        riskEvaluation.setCreditApplicationId(application.getId());
        riskEvaluation.setEvaluatedAt(LocalDateTime.now());
        riskEvaluationRepository.save(riskEvaluation);

        // Step 7: Update application status
        if (rejectionReason == null) {
            application.setStatus("APPROVED");
            application.setRejectionReason(null);
        } else {
            application.setStatus("REJECTED");
            application.setRejectionReason(rejectionReason);
        }
        application.setUpdatedAt(LocalDateTime.now());

        // Step 8: Save and return
        return creditApplicationRepository.save(application);
    }

    /**
     * Evaluates internal business policies
     * 
     * @return null if approved, rejection reason if rejected
     */
    private String evaluateInternalPolicies(CreditApplication application,
            Affiliate affiliate,
            RiskEvaluation riskEvaluation) {

        // Policy 1: Check risk level - ALTO risk is automatically rejected
        if ("ALTO".equals(riskEvaluation.getRiskLevel())) {
            return "High risk level - Score: " + riskEvaluation.getScore();
        }

        // Policy 2: Check installment/income ratio
        BigDecimal monthlyInstallment = calculateMonthlyInstallment(
                application.getRequestedAmount(),
                application.getTermMonths(),
                application.getProposedRate());

        BigDecimal installmentIncomeRatio = monthlyInstallment
                .divide(affiliate.getSalary(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        if (installmentIncomeRatio.compareTo(MAX_INSTALLMENT_INCOME_RATIO) > 0) {
            return String.format("Installment/income ratio of %.2f%% exceeds maximum allowed (%.0f%%). " +
                    "Monthly installment: %s, Salary: %s",
                    installmentIncomeRatio.doubleValue(),
                    MAX_INSTALLMENT_INCOME_RATIO.doubleValue(),
                    monthlyInstallment,
                    affiliate.getSalary());
        }

        // Policy 3: Check maximum amount vs salary
        BigDecimal maximumAllowedAmount = affiliate.getSalary().multiply(MAXIMUM_AMOUNT_MULTIPLIER);
        if (application.getRequestedAmount().compareTo(maximumAllowedAmount) > 0) {
            return String.format("Requested amount %s exceeds maximum allowed %s (10x salary)",
                    application.getRequestedAmount(),
                    maximumAllowedAmount);
        }

        // Policy 4: Check minimum affiliation time
        long monthsSinceAffiliation = ChronoUnit.MONTHS.between(
                affiliate.getAffiliationDate(),
                LocalDate.now());

        if (monthsSinceAffiliation < MINIMUM_AFFILIATION_MONTHS) {
            return String.format("Affiliate must have at least %d months of affiliation. Current: %d months",
                    MINIMUM_AFFILIATION_MONTHS,
                    monthsSinceAffiliation);
        }

        // All policies passed
        return null;
    }

    /**
     * Calculates monthly installment using French amortization system
     */
    private BigDecimal calculateMonthlyInstallment(BigDecimal amount,
            Integer termMonths,
            BigDecimal annualRate) {
        if (amount == null || termMonths == null || termMonths == 0 || annualRate == null) {
            return BigDecimal.ZERO;
        }

        // Monthly interest rate
        BigDecimal monthlyRate = annualRate
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);

        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return amount.divide(BigDecimal.valueOf(termMonths), 2, RoundingMode.HALF_UP);
        }

        // Formula: M = P * [r(1+r)^n] / [(1+r)^n - 1]
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal power = onePlusRate.pow(termMonths);
        BigDecimal numerator = amount.multiply(monthlyRate).multiply(power);
        BigDecimal denominator = power.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
}
