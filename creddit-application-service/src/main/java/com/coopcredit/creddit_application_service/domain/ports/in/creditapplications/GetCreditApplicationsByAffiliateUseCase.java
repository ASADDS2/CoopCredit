package com.coopcredit.creddit_application_service.domain.ports.in.creditapplications;

import com.coopcredit.creddit_application_service.domain.model.CreditApplication;

import java.util.List;

public interface GetCreditApplicationsByAffiliateUseCase {

    List<CreditApplication> execute(Long affiliateId);
}
