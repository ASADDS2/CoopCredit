package com.coopcredit.creddit_application_service.application.usecases.affiliate;

import com.coopcredit.creddit_application_service.domain.exceptions.BusinessException;
import com.coopcredit.creddit_application_service.domain.model.Affiliate;
import com.coopcredit.creddit_application_service.domain.model.AffiliateStatus;
import com.coopcredit.creddit_application_service.domain.ports.out.AffiliateRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterAffiliateUseCaseTest {

    @Mock
    private AffiliateRepositoryPort affiliateRepository;

    private RegisterAffiliateUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterAffiliateUseCase(affiliateRepository);
    }

    @Test
    @DisplayName("Should register a new affiliate successfully")
    void shouldRegisterAffiliateSuccessfully() {
        // Given
        Affiliate affiliate = createValidAffiliate();
        when(affiliateRepository.existsByDocumentId(anyString())).thenReturn(false);
        when(affiliateRepository.save(any(Affiliate.class))).thenReturn(affiliate);

        // When
        Affiliate result = useCase.execute(affiliate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDocumentId()).isEqualTo("12345678");
        assertThat(result.getStatus()).isEqualTo(AffiliateStatus.ACTIVE);
        verify(affiliateRepository).existsByDocumentId("12345678");
        verify(affiliateRepository).save(affiliate);
    }

    @Test
    @DisplayName("Should throw exception when document already exists")
    void shouldThrowExceptionWhenDocumentExists() {
        // Given
        Affiliate affiliate = createValidAffiliate();
        when(affiliateRepository.existsByDocumentId(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> useCase.execute(affiliate))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Affiliate with document 12345678 already exists");

        verify(affiliateRepository).existsByDocumentId("12345678");
        verify(affiliateRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should validate required fields")
    void shouldValidateRequiredFields() {
        // Given
        Affiliate affiliate = new Affiliate();
        affiliate.setDocumentId("");
        affiliate.setFirstName("");
        affiliate.setLastName("");

        // When & Then
        assertThatThrownBy(() -> useCase.execute(affiliate))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("required");

        verify(affiliateRepository, never()).existsByDocumentId(anyString());
        verify(affiliateRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should validate salary is positive")
    void shouldValidateSalaryIsPositive() {
        // Given
        Affiliate affiliate = createValidAffiliate();
        affiliate.setSalary(BigDecimal.ZERO);

        // When & Then
        assertThatThrownBy(() -> useCase.execute(affiliate))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Salary must be greater than zero");

        verify(affiliateRepository, never()).save(any());
    }

    private Affiliate createValidAffiliate() {
        Affiliate affiliate = new Affiliate();
        affiliate.setDocumentId("12345678");
        affiliate.setFirstName("John");
        affiliate.setLastName("Doe");
        affiliate.setEmail("john.doe@example.com");
        affiliate.setPhone("1234567890");
        affiliate.setAddress("123 Main St");
        affiliate.setCity("New York");
        affiliate.setDepartment("NY");
        affiliate.setSalary(new BigDecimal("50000"));
        affiliate.setHireDate(LocalDate.now().minusYears(2));
        affiliate.setStatus(AffiliateStatus.ACTIVE);
        return affiliate;
    }
}
