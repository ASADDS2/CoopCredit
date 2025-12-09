package com.coopcredit.creddit_application_service.infrastructure.mappers.creditapplications;

import com.coopcredit.creddit_application_service.domain.model.CreditApplication;
import com.coopcredit.creddit_application_service.infrastructure.web.dto.creditapplications.CreateCreditApplicationRequest;
import com.coopcredit.creddit_application_service.infrastructure.web.dto.creditapplications.CreditApplicationResponse;
import com.coopcredit.creddit_application_service.infrastructure.web.dto.creditapplications.RiskEvaluationResponse;
import org.springframework.stereotype.Component;

@Component
public class CreditApplicationDtoMapper {

    public CreditApplication toDomain(CreateCreditApplicationRequest request) {
        if (request == null) {
            return null;
        }

        CreditApplication application = new CreditApplication();
        application.setAffiliateId(request.getAffiliateId());
        application.setRequestedAmount(request.getRequestedAmount());
        application.setTermMonths(request.getTermMonths());
        application.setProposedRate(request.getProposedRate());
        return application;
    }

    public CreditApplicationResponse toResponse(CreditApplication domain) {
        if (domain == null) {
            return null;
        }

        return new CreditApplicationResponse(
                domain.getId(),
                domain.getAffiliateId(),
                domain.getRequestedAmount(),
                domain.getTermMonths(),
                domain.getProposedRate(),
                domain.getApplicationDate(),
                domain.getStatus(),
                domain.getRejectionReason(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                null // riskEvaluation will be set separately if needed
        );
    }
}
