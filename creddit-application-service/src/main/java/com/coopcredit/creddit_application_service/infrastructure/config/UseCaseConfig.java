package com.coopcredit.creddit_application_service.infrastructure.config;

import com.coopcredit.creddit_application_service.application.usecases.affiliates.GetAffiliateUseCaseImpl;
import com.coopcredit.creddit_application_service.application.usecases.affiliates.RegisterAffiliateUseCaseImpl;
import com.coopcredit.creddit_application_service.application.usecases.affiliates.UpdateAffiliateUseCaseImpl;
import com.coopcredit.creddit_application_service.application.usecases.auth.AuthenticateUserUseCaseImpl;
import com.coopcredit.creddit_application_service.application.usecases.auth.RegisterUserUseCaseImpl;
import com.coopcredit.creddit_application_service.application.usecases.creditapplications.*;
import com.coopcredit.creddit_application_service.domain.ports.in.affiliates.GetAffiliateUseCase;
import com.coopcredit.creddit_application_service.domain.ports.in.affiliates.RegisterAffiliateUseCase;
import com.coopcredit.creddit_application_service.domain.ports.in.affiliates.UpdateAffiliateUseCase;
import com.coopcredit.creddit_application_service.domain.ports.in.auth.AuthenticateUserUseCase;
import com.coopcredit.creddit_application_service.domain.ports.in.auth.RegisterUserUseCase;
import com.coopcredit.creddit_application_service.domain.ports.in.creditapplications.*;
import com.coopcredit.creddit_application_service.domain.ports.out.*;
import com.coopcredit.creddit_application_service.infrastructure.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UseCaseConfig {

    // Affiliate Use Cases

    @Bean
    public RegisterAffiliateUseCase registerAffiliateUseCase(AffiliateRepositoryPort affiliateRepository) {
        return new RegisterAffiliateUseCaseImpl(affiliateRepository);
    }

    @Bean
    public GetAffiliateUseCase getAffiliateUseCase(AffiliateRepositoryPort affiliateRepository) {
        return new GetAffiliateUseCaseImpl(affiliateRepository);
    }

    @Bean
    public UpdateAffiliateUseCase updateAffiliateUseCase(AffiliateRepositoryPort affiliateRepository) {
        return new UpdateAffiliateUseCaseImpl(affiliateRepository);
    }

    // Credit Application Use Cases

    @Bean
    public RegisterCreditApplicationUseCase registerCreditApplicationUseCase(
            CreditApplicationRepositoryPort creditApplicationRepository,
            AffiliateRepositoryPort affiliateRepository) {
        return new RegisterCreditApplicationUseCaseImpl(creditApplicationRepository, affiliateRepository);
    }

    @Bean
    public EvaluateCreditApplicationUseCase evaluateCreditApplicationUseCase(
            CreditApplicationRepositoryPort creditApplicationRepository,
            AffiliateRepositoryPort affiliateRepository,
            RiskEvaluationRepositoryPort riskEvaluationRepository,
            RiskCentralPort riskCentralPort) {
        return new EvaluateCreditApplicationUseCaseImpl(
                creditApplicationRepository,
                affiliateRepository,
                riskEvaluationRepository,
                riskCentralPort);
    }

    @Bean
    public GetCreditApplicationUseCase getCreditApplicationUseCase(
            CreditApplicationRepositoryPort creditApplicationRepository) {
        return new GetCreditApplicationUseCaseImpl(creditApplicationRepository);
    }

    @Bean
    public GetCreditApplicationsByAffiliateUseCase getCreditApplicationsByAffiliateUseCase(
            CreditApplicationRepositoryPort creditApplicationRepository) {
        return new GetCreditApplicationsByAffiliateUseCaseImpl(creditApplicationRepository);
    }

    @Bean
    public GetPendingApplicationsUseCase getPendingApplicationsUseCase(
            CreditApplicationRepositoryPort creditApplicationRepository) {
        return new GetPendingApplicationsUseCaseImpl(creditApplicationRepository);
    }

    // Auth Use Cases

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepositoryPort userRepository,
            PasswordEncoder passwordEncoder) {
        return new RegisterUserUseCaseImpl(userRepository, passwordEncoder);
    }

    @Bean
    public AuthenticateUserUseCase authenticateUserUseCase(
            UserRepositoryPort userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        return new AuthenticateUserUseCaseImpl(userRepository, passwordEncoder, jwtService);
    }
}
