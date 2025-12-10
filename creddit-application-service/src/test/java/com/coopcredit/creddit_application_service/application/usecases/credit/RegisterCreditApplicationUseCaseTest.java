package com.coopcredit.creddit_application_service.application.usecases.credit;

import com.coopcredit.creddit_application_service.domain.exceptions.BusinessException;
import com.coopcredit.creddit_application_service.domain.model.*;
import com.coopcredit.creddit_application_service.domain.ports.out.AffiliateRepositoryPort;
import com.coopcredit.creddit_application_service.domain.ports.out.CreditApplicationRepositoryPort;
import com.coopcredit.creddit_application_service.domain.ports.out.RiskEvaluationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterCreditApplicationUseCaseTest {

    @Mock
    private CreditApplicationRepositoryPort creditApplicationRepository;

    @Mock
    private AffiliateRepositoryPort affiliateRepository;

    @Mock
    private RiskEvaluationPort riskEvaluationPort;

    private RegisterCreditApplicationUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterCreditApplicationUseCase(
                creditApplicationRepository,
                affiliateRepository,
                riskEvaluationPort);
    }

    @Test
    @DisplayName("Should register credit application successfully with automatic risk evaluation")
    void shouldRegisterCreditApplicationSuccessfully() {
        // Given
        Long affiliateId = 1L;
        Affiliate affiliate = createActiveAffiliate(affiliateId);
        CreditApplication application = createValidApplication(affiliateId);
        RiskEvaluation riskEvaluation = createRiskEvaluation();

        when(affiliateRepository.findById(affiliateId)).thenReturn(Optional.of(affiliate));
        when(creditApplicationRepository.save(any(CreditApplication.class))).thenReturn(application);
        when(riskEvaluationPort.evaluateRisk(anyString(), any(BigDecimal.class), anyInt()))
                .thenReturn(riskEvaluation);

        // When
        CreditApplication result = useCase.execute(application);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.PENDING);
        assertThat(result.getRiskEvaluation()).isNotNull();
        verify(affiliateRepository).findById(affiliateId);
        verify(creditApplicationRepository).save(application);
        verify(riskEvaluationPort).evaluateRisk(affiliate.getDocumentId(), application.getAmount(),
                application.getTerm());
    }

    @Test
    @DisplayName("Should throw exception when affiliate not found")
    void shouldThrowExceptionWhenAffiliateNotFound() {
        // Given
        Long affiliateId = 999L;
        CreditApplication application = createValidApplication(affiliateId);

        when(affiliateRepository.findById(affiliateId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> useCase.execute(application))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Affiliate not found with id: " + affiliateId);

        verify(affiliateRepository).findById(affiliateId);
        verify(creditApplicationRepository, never()).save(any());
        verify(riskEvaluationPort, never()).evaluateRisk(anyString(), any(), anyInt());
    }

    @Test
    @DisplayName("Should throw exception when affiliate is inactive")
    void shouldThrowExceptionWhenAffiliateInactive() {
        // Given
        Long affiliateId = 1L;
        Affiliate affiliate = createInactiveAffiliate(affiliateId);
        CreditApplication application = createValidApplication(affiliateId);

        when(affiliateRepository.findById(affiliateId)).thenReturn(Optional.of(affiliate));

        // When & Then
        assertThatThrownBy(() -> useCase.execute(application))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Affiliate must be active to apply for credit");

        verify(affiliateRepository).findById(affiliateId);
        verify(creditApplicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should validate maximum amount based on salary")
    void shouldValidateMaximumAmountBasedOnSalary() {
        // Given
        Long affiliateId = 1L;
        Affiliate affiliate = createActiveAffiliate(affiliateId);
        affiliate.setSalary(new BigDecimal("1000")); // Low salary

        CreditApplication application = createValidApplication(affiliateId);
        application.setAmount(new BigDecimal("50000")); // High amount

        when(affiliateRepository.findById(affiliateId)).thenReturn(Optional.of(affiliate));

        // When & Then
        assertThatThrownBy(() -> useCase.execute(application))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Credit amount exceeds maximum allowed based on salary");

        verify(creditApplicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should validate minimum seniority requirement")
    void shouldValidateMinimumSeniorityRequirement() {
        // Given
        Long affiliateId = 1L;
        Affiliate affiliate = createActiveAffiliate(affiliateId);
        affiliate.setHireDate(LocalDate.now().minusMonths(3)); // Only 3 months

        CreditApplication application = createValidApplication(affiliateId);

        when(affiliateRepository.findById(affiliateId)).thenReturn(Optional.of(affiliate));

        // When & Then
        assertThatThrownBy(() -> useCase.execute(application))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Minimum seniority of 6 months required");

        verify(creditApplicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should validate debt-to-income ratio")
    void shouldValidateDebtToIncomeRatio() {
        // Given
        Long affiliateId = 1L;
        Affiliate affiliate = createActiveAffiliate(affiliateId);
        affiliate.setSalary(new BigDecimal("2000"));

        CreditApplication application = createValidApplication(affiliateId);
        application.setAmount(new BigDecimal("10000"));
        application.setTerm(12); // Monthly payment would be ~833, which is > 40% of 2000

        when(affiliateRepository.findById(affiliateId)).thenReturn(Optional.of(affiliate));

        // When & Then
        assertThatThrownBy(() -> useCase.execute(application))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Monthly payment exceeds 40% of income");

        verify(creditApplicationRepository, never()).save(any());
    }

    private Affiliate createActiveAffiliate(Long id) {
        Affiliate affiliate = new Affiliate();
        affiliate.setId(id);
        affiliate.setDocumentId("12345678");
        affiliate.setFirstName("John");
        affiliate.setLastName("Doe");
        affiliate.setStatus(AffiliateStatus.ACTIVE);
        affiliate.setSalary(new BigDecimal("5000"));
        affiliate.setHireDate(LocalDate.now().minusYears(2));
        return affiliate;
    }

    private Affiliate createInactiveAffiliate(Long id) {
        Affiliate affiliate = createActiveAffiliate(id);
        affiliate.setStatus(AffiliateStatus.INACTIVE);
        return affiliate;
    }

    private CreditApplication createValidApplication(Long affiliateId) {
        CreditApplication application = new CreditApplication();
        application.setAffiliateId(affiliateId);
        application.setAmount(new BigDecimal("10000"));
        application.setTerm(24);
        application.setPurpose("Home improvement");
        application.setStatus(ApplicationStatus.PENDING);
        return application;
    }

    private RiskEvaluation createRiskEvaluation() {
        RiskEvaluation evaluation = new RiskEvaluation();
        evaluation.setScore(750);
        evaluation.setRiskLevel(RiskLevel.LOW);
        evaluation.setRecommendation("APPROVED");
        evaluation.setMaxAmount(new BigDecimal("15000"));
        return evaluation;
    }
}
