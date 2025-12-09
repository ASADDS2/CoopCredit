package com.coopcredit.creddit_application_service.application.usecases.creditapplications;

import com.coopcredit.creddit_application_service.domain.model.CreditApplication;
import com.coopcredit.creddit_application_service.domain.ports.in.creditapplications.GetCreditApplicationsByAffiliateUseCase;
import com.coopcredit.creddit_application_service.domain.ports.out.CreditApplicationRepositoryPort;

import java.util.List;

public class GetCreditApplicationsByAffiliateUseCaseImpl implements GetCreditApplicationsByAffiliateUseCase {

    private final CreditApplicationRepositoryPort creditApplicationRepository;

    public GetCreditApplicationsByAffiliateUseCaseImpl(CreditApplicationRepositoryPort creditApplicationRepository) {
        this.creditApplicationRepository = creditApplicationRepository;
    }

    @Override
    public List<CreditApplication> execute(Long affiliateId) {
        return creditApplicationRepository.findByAffiliateId(affiliateId);
    }
}
