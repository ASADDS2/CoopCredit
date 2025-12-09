package com.coopcredit.creddit_application_service.infrastructure.web.dto.affiliates;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAffiliateRequest {

    @NotBlank(message = "Document is required")
    private String document;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Salary is required")
    @Positive(message = "Salary must be positive")
    private BigDecimal salary;

    @NotNull(message = "Affiliation date is required")
    private LocalDate affiliationDate;
}
