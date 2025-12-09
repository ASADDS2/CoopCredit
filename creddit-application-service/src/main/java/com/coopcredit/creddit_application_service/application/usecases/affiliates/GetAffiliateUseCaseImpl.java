package com.coopcredit.creddit_application_service.application.usecases.affiliates;

import com.coopcredit.creddit_application_service.domain.exception.NotFoundException;
import com.coopcredit.creddit_application_service.domain.model.Affiliate;
import com.coopcredit.creddit_application_service.domain.ports.in.affiliates.GetAffiliateUseCase;
import com.coopcredit.creddit_application_service.domain.ports.out.AffiliateRepositoryPort;

public class GetAffiliateUseCaseImpl implements GetAffiliateUseCase {

    private final AffiliateRepositoryPort affiliateRepository;

    public GetAffiliateUseCaseImpl(AffiliateRepositoryPort affiliateRepository) {
        this.affiliateRepository = affiliateRepository;
    }

    @Override
    public Affiliate execute(Long id) {
        return affiliateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Affiliate", id));
    }
}
