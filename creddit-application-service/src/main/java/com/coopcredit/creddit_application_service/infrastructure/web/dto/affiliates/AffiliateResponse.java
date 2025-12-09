package com.coopcredit.creddit_application_service.infrastructure.web.dto.affiliates;

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
public class AffiliateResponse {

    private Long id;
    private String document;
    private String name;
    private BigDecimal salary;
    private LocalDate affiliationDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
