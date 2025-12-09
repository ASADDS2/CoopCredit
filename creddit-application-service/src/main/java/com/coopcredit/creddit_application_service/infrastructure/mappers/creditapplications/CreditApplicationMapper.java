package com.coopcredit.creddit_application_service.infrastructure.mappers.creditapplications;

import com.coopcredit.creddit_application_service.domain.model.CreditApplication;
import com.coopcredit.creddit_application_service.infrastructure.entities.CreditApplicationEntity;
import org.springframework.stereotype.Component;

@Component
public class CreditApplicationMapper {

    public CreditApplication toDomain(CreditApplicationEntity entity) {
        if (entity == null) {
            return null;
        }

        return new CreditApplication(
                entity.getId(),
                entity.getAffiliate() != null ? entity.getAffiliate().getId() : null,
                entity.getRequestedAmount(),
                entity.getTermMonths(),
                entity.getProposedRate(),
                entity.getApplicationDate(),
                entity.getStatus(),
                entity.getRejectionReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public CreditApplicationEntity toEntity(CreditApplication domain) {
        if (domain == null) {
            return null;
        }

        CreditApplicationEntity entity = new CreditApplicationEntity();
        entity.setId(domain.getId());
        // Note: affiliate relationship will be set in the adapter
        entity.setRequestedAmount(domain.getRequestedAmount());
        entity.setTermMonths(domain.getTermMonths());
        entity.setProposedRate(domain.getProposedRate());
        entity.setApplicationDate(domain.getApplicationDate());
        entity.setStatus(domain.getStatus());
        entity.setRejectionReason(domain.getRejectionReason());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
