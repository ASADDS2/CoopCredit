package com.coopcredit.creddit_application_service.infrastructure.mappers.affiliates;

import com.coopcredit.creddit_application_service.domain.model.Affiliate;
import com.coopcredit.creddit_application_service.infrastructure.web.dto.affiliates.AffiliateResponse;
import com.coopcredit.creddit_application_service.infrastructure.web.dto.affiliates.CreateAffiliateRequest;
import org.springframework.stereotype.Component;

@Component
public class AffiliateDtoMapper {

    public Affiliate toDomain(CreateAffiliateRequest request) {
        if (request == null) {
            return null;
        }

        Affiliate affiliate = new Affiliate();
        affiliate.setDocument(request.getDocument());
        affiliate.setName(request.getName());
        affiliate.setSalary(request.getSalary());
        affiliate.setAffiliationDate(request.getAffiliationDate());
        return affiliate;
    }

    public AffiliateResponse toResponse(Affiliate domain) {
        if (domain == null) {
            return null;
        }

        return new AffiliateResponse(
                domain.getId(),
                domain.getDocument(),
                domain.getName(),
                domain.getSalary(),
                domain.getAffiliationDate(),
                domain.getStatus(),
                domain.getCreatedAt(),
                domain.getUpdatedAt());
    }
}
