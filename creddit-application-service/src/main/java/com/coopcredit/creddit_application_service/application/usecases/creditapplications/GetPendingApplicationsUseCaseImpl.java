package com.coopcredit.creddit_application_service.application.usecases.creditapplications;

import com.coopcredit.creddit_application_service.domain.model.CreditApplication;
import com.coopcredit.creddit_application_service.domain.ports.in.creditapplications.GetPendingApplicationsUseCase;
import com.coopcredit.creddit_application_service.domain.ports.out.CreditApplicationRepositoryPort;

import java.util.List;

public class GetPendingApplicationsUseCaseImpl implements GetPendingApplicationsUseCase {

    private final CreditApplicationRepositoryPort creditApplicationRepository;

    public GetPendingApplicationsUseCaseImpl(CreditApplicationRepositoryPort creditApplicationRepository) {
        this.creditApplicationRepository = creditApplicationRepository;
    }

    @Override
    public List<CreditApplication> execute() {
        return creditApplicationRepository.findByStatus("PENDING");
    }
}
