package com.coopcredit.creddit_application_service.infrastructure.web.dto.creditapplications;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditApplicationResponse {

    private Long id;
    private Long affiliateId;
    private BigDecimal requestedAmount;
    private Integer termMonths;
    private BigDecimal proposedRate;
    private LocalDate applicationDate;
    private String status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private RiskEvaluationResponse riskEvaluation;
}
