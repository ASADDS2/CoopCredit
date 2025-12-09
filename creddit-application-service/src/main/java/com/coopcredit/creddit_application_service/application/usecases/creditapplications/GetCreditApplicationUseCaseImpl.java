package com.coopcredit.creddit_application_service.application.usecases.creditapplications;

import com.coopcredit.creddit_application_service.domain.exception.NotFoundException;
import com.coopcredit.creddit_application_service.domain.model.CreditApplication;
import com.coopcredit.creddit_application_service.domain.ports.in.creditapplications.GetCreditApplicationUseCase;
import com.coopcredit.creddit_application_service.domain.ports.out.CreditApplicationRepositoryPort;

public class GetCreditApplicationUseCaseImpl implements GetCreditApplicationUseCase {

    private final CreditApplicationRepositoryPort creditApplicationRepository;

    public GetCreditApplicationUseCaseImpl(CreditApplicationRepositoryPort creditApplicationRepository) {
        this.creditApplicationRepository = creditApplicationRepository;
    }

    @Override
    public CreditApplication execute(Long id) {
        return creditApplicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("CreditApplication", id));
    }
}
