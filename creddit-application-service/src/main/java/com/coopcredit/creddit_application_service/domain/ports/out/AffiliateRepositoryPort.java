package com.coopcredit.creddit_application_service.domain.ports.out;

import com.coopcredit.creddit_application_service.domain.model.Affiliate;

import java.util.List;
import java.util.Optional;

public interface AffiliateRepositoryPort {

    Affiliate save(Affiliate affiliate);

    Optional<Affiliate> findById(Long id);

    Optional<Affiliate> findByDocument(String document);

    List<Affiliate> findAll();

    List<Affiliate> findByStatus(String status);

    boolean existsByDocument(String document);

    void deleteById(Long id);
}
