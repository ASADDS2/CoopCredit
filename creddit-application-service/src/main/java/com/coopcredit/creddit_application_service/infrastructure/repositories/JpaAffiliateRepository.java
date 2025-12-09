package com.coopcredit.creddit_application_service.infrastructure.repositories;

import com.coopcredit.creddit_application_service.infrastructure.entities.AffiliateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaAffiliateRepository extends JpaRepository<AffiliateEntity, Long> {

    Optional<AffiliateEntity> findByDocument(String document);

    boolean existsByDocument(String document);

    List<AffiliateEntity> findByStatus(String status);

    @Query("SELECT a FROM AffiliateEntity a LEFT JOIN FETCH a.creditApplications WHERE a.id = :id")
    Optional<AffiliateEntity> findByIdWithApplications(Long id);
}
