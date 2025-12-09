package com.coopcredit.creddit_application_service.domain.exception;

import java.math.BigDecimal;

public class InsufficientIncomeException extends BusinessRuleException {

    public InsufficientIncomeException(BigDecimal monthlyInstallment, BigDecimal salary, BigDecimal ratio) {
        super(String.format(
                "Installment/income ratio of %.2f%% exceeds maximum allowed. Monthly installment: %s, Salary: %s",
                ratio.doubleValue(), monthlyInstallment, salary));
    }

    public InsufficientIncomeException(String message) {
        super(message);
    }
}
