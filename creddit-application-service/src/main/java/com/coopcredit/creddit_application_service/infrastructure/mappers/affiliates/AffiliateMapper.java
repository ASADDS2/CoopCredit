package com.coopcredit.creddit_application_service.infrastructure.mappers.affiliates;

import com.coopcredit.creddit_application_service.domain.model.Affiliate;
import com.coopcredit.creddit_application_service.infrastructure.entities.AffiliateEntity;
import org.springframework.stereotype.Component;

@Component
public class AffiliateMapper {

    public Affiliate toDomain(AffiliateEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Affiliate(
                entity.getId(),
                entity.getDocument(),
                entity.getName(),
                entity.getSalary(),
                entity.getAffiliationDate(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public AffiliateEntity toEntity(Affiliate domain) {
        if (domain == null) {
            return null;
        }

        AffiliateEntity entity = new AffiliateEntity();
        entity.setId(domain.getId());
        entity.setDocument(domain.getDocument());
        entity.setName(domain.getName());
        entity.setSalary(domain.getSalary());
        entity.setAffiliationDate(domain.getAffiliationDate());
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
