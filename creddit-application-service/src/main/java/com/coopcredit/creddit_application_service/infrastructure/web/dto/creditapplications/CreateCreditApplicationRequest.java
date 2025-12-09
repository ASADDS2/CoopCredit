package com.coopcredit.creddit_application_service.infrastructure.web.dto.creditapplications;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCreditApplicationRequest {

    @NotNull(message = "Affiliate ID is required")
    private Long affiliateId;

    @NotNull(message = "Requested amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal requestedAmount;

    @NotNull(message = "Term in months is required")
    @Positive(message = "Term must be positive")
    private Integer termMonths;

    @NotNull(message = "Proposed rate is required")
    @Positive(message = "Rate must be positive")
    private BigDecimal proposedRate;
}
