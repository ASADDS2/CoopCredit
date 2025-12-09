package com.coopcredit.creddit_application_service.infrastructure.controllers;

import com.coopcredit.creddit_application_service.domain.model.CreditApplication;
import com.coopcredit.creddit_application_service.domain.ports.in.creditapplications.*;
import com.coopcredit.creddit_application_service.infrastructure.mappers.creditapplications.CreditApplicationDtoMapper;
import com.coopcredit.creddit_application_service.infrastructure.web.dto.creditapplications.CreateCreditApplicationRequest;
import com.coopcredit.creddit_application_service.infrastructure.web.dto.creditapplications.CreditApplicationResponse;
import com.coopcredit.creddit_application_service.infrastructure.web.response.AppResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/credit-applications")
@Tag(name = "Credit Applications", description = "Credit application management endpoints")
@SecurityRequirement(name = "bearer-jwt")
public class CreditApplicationController {

    private final RegisterCreditApplicationUseCase registerCreditApplicationUseCase;
    private final EvaluateCreditApplicationUseCase evaluateCreditApplicationUseCase;
    private final GetCreditApplicationUseCase getCreditApplicationUseCase;
    private final GetCreditApplicationsByAffiliateUseCase getByAffiliateUseCase;
    private final GetPendingApplicationsUseCase getPendingApplicationsUseCase;
    private final CreditApplicationDtoMapper dtoMapper;

    public CreditApplicationController(
            RegisterCreditApplicationUseCase registerCreditApplicationUseCase,
            EvaluateCreditApplicationUseCase evaluateCreditApplicationUseCase,
            GetCreditApplicationUseCase getCreditApplicationUseCase,
            GetCreditApplicationsByAffiliateUseCase getByAffiliateUseCase,
            GetPendingApplicationsUseCase getPendingApplicationsUseCase,
            CreditApplicationDtoMapper dtoMapper) {
        this.registerCreditApplicationUseCase = registerCreditApplicationUseCase;
        this.evaluateCreditApplicationUseCase = evaluateCreditApplicationUseCase;
        this.getCreditApplicationUseCase = getCreditApplicationUseCase;
        this.getByAffiliateUseCase = getByAffiliateUseCase;
        this.getPendingApplicationsUseCase = getPendingApplicationsUseCase;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('AFILIADO')")
    @Operation(summary = "Submit a new credit application (Affiliate only)")
    public ResponseEntity<AppResponse<CreditApplicationResponse>> submitApplication(
            @Valid @RequestBody CreateCreditApplicationRequest request) {

        CreditApplication application = dtoMapper.toDomain(request);
        CreditApplication created = registerCreditApplicationUseCase.execute(application);
        CreditApplicationResponse response = dtoMapper.toResponse(created);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AppResponse.success("Credit application submitted successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AFILIADO', 'ANALISTA', 'ADMIN')")
    @Operation(summary = "Get credit application by ID")
    public ResponseEntity<AppResponse<CreditApplicationResponse>> getApplication(@PathVariable Long id) {
        CreditApplication application = getCreditApplicationUseCase.execute(id);
        CreditApplicationResponse response = dtoMapper.toResponse(application);

        return ResponseEntity.ok(AppResponse.success(response));
    }

    @GetMapping("/affiliate/{affiliateId}")
    @PreAuthorize("hasAnyRole('AFILIADO', 'ADMIN')")
    @Operation(summary = "Get all applications for an affiliate")
    public ResponseEntity<AppResponse<List<CreditApplicationResponse>>> getByAffiliate(
            @PathVariable Long affiliateId) {

        List<CreditApplication> applications = getByAffiliateUseCase.execute(affiliateId);
        List<CreditApplicationResponse> responses = applications.stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(AppResponse.success(responses));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ANALISTA', 'ADMIN')")
    @Operation(summary = "Get all pending applications (Analyst/Admin only)")
    public ResponseEntity<AppResponse<List<CreditApplicationResponse>>> getPendingApplications() {
        List<CreditApplication> applications = getPendingApplicationsUseCase.execute();
        List<CreditApplicationResponse> responses = applications.stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(AppResponse.success(responses));
    }

    @PostMapping("/{id}/evaluate")
    @PreAuthorize("hasAnyRole('ANALISTA', 'ADMIN')")
    @Operation(summary = "Evaluate a credit application (Analyst/Admin only)")
    public ResponseEntity<AppResponse<CreditApplicationResponse>> evaluateApplication(@PathVariable Long id) {
        CreditApplication evaluated = evaluateCreditApplicationUseCase.execute(id);
        CreditApplicationResponse response = dtoMapper.toResponse(evaluated);

        return ResponseEntity.ok(AppResponse.success("Application evaluated successfully", response));
    }
}
