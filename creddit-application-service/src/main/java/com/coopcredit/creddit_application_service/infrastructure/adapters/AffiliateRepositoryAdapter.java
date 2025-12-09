package com.coopcredit.creddit_application_service.infrastructure.adapters;

import com.coopcredit.creddit_application_service.domain.model.Affiliate;
import com.coopcredit.creddit_application_service.domain.ports.out.AffiliateRepositoryPort;
import com.coopcredit.creddit_application_service.infrastructure.entities.AffiliateEntity;
import com.coopcredit.creddit_application_service.infrastructure.mappers.affiliates.AffiliateMapper;
import com.coopcredit.creddit_application_service.infrastructure.repositories.JpaAffiliateRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AffiliateRepositoryAdapter implements AffiliateRepositoryPort {

    private final JpaAffiliateRepository jpaRepository;
    private final AffiliateMapper mapper;

    public AffiliateRepositoryAdapter(JpaAffiliateRepository jpaRepository, AffiliateMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Affiliate save(Affiliate affiliate) {
        AffiliateEntity entity = mapper.toEntity(affiliate);
        AffiliateEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Affiliate> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Affiliate> findByDocument(String document) {
        return jpaRepository.findByDocument(document)
                .map(mapper::toDomain);
    }

    @Override
    public List<Affiliate> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Affiliate> findByStatus(String status) {
        return jpaRepository.findByStatus(status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByDocument(String document) {
        return jpaRepository.existsByDocument(document);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
