package com.coopcredit.creddit_application_service.domain.ports.in.creditapplications;

import com.coopcredit.creddit_application_service.domain.model.CreditApplication;

public interface GetCreditApplicationUseCase {

    CreditApplication execute(Long id);
}
