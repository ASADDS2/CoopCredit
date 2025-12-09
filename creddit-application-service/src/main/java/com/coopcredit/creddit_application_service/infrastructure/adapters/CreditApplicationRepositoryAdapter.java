package com.coopcredit.creddit_application_service.infrastructure.adapters;

import com.coopcredit.creddit_application_service.domain.model.CreditApplication;
import com.coopcredit.creddit_application_service.domain.ports.out.CreditApplicationRepositoryPort;
import com.coopcredit.creddit_application_service.infrastructure.entities.AffiliateEntity;
import com.coopcredit.creddit_application_service.infrastructure.entities.CreditApplicationEntity;
import com.coopcredit.creddit_application_service.infrastructure.mappers.creditapplications.CreditApplicationMapper;
import com.coopcredit.creddit_application_service.infrastructure.repositories.JpaAffiliateRepository;
import com.coopcredit.creddit_application_service.infrastructure.repositories.JpaCreditApplicationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CreditApplicationRepositoryAdapter implements CreditApplicationRepositoryPort {

    private final JpaCreditApplicationRepository jpaRepository;
    private final JpaAffiliateRepository affiliateRepository;
    private final CreditApplicationMapper mapper;

    public CreditApplicationRepositoryAdapter(
            JpaCreditApplicationRepository jpaRepository,
            JpaAffiliateRepository affiliateRepository,
            CreditApplicationMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.affiliateRepository = affiliateRepository;
        this.mapper = mapper;
    }

    @Override
    public CreditApplication save(CreditApplication creditApplication) {
        CreditApplicationEntity entity = mapper.toEntity(creditApplication);

        // Set affiliate relationship
        if (creditApplication.getAffiliateId() != null) {
            AffiliateEntity affiliate = affiliateRepository.findById(creditApplication.getAffiliateId())
                    .orElseThrow(() -> new RuntimeException("Affiliate not found"));
            entity.setAffiliate(affiliate);
        }

        CreditApplicationEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<CreditApplication> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<CreditApplication> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CreditApplication> findByAffiliateId(Long affiliateId) {
        return jpaRepository.findByAffiliateId(affiliateId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CreditApplication> findByStatus(String status) {
        return jpaRepository.findByStatus(status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
