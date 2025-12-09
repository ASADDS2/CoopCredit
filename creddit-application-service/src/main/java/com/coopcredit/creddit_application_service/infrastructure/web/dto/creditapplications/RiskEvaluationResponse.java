package com.coopcredit.creddit_application_service.infrastructure.web.dto.creditapplications;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RiskEvaluationResponse {

    private Long id;
    private String document;
    private Integer score;
    private String riskLevel;
    private String detail;
    private LocalDateTime evaluatedAt;
}
