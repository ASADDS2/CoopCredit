package com.coopcredit.creddit_application_service.infrastructure.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA Configuration to ensure all repositories and entities are properly
 * scanned
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.coopcredit.creddit_application_service.infrastructure.repositories")
@EntityScan(basePackages = "com.coopcredit.creddit_application_service.infrastructure.entities")
@ComponentScan(basePackages = {
        "com.coopcredit.creddit_application_service.infrastructure.adapters",
        "com.coopcredit.creddit_application_service.infrastructure.mappers"
})
@EnableTransactionManagement
public class JpaConfig {
    // Configuration class to ensure JPA repositories are properly configured
}
