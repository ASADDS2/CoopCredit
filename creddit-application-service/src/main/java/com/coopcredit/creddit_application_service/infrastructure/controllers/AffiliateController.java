package com.coopcredit.creddit_application_service.infrastructure.controllers;

import com.coopcredit.creddit_application_service.domain.model.Affiliate;
import com.coopcredit.creddit_application_service.domain.ports.in.affiliates.GetAffiliateUseCase;
import com.coopcredit.creddit_application_service.domain.ports.in.affiliates.RegisterAffiliateUseCase;
import com.coopcredit.creddit_application_service.domain.ports.in.affiliates.UpdateAffiliateUseCase;
import com.coopcredit.creddit_application_service.infrastructure.mappers.affiliates.AffiliateDtoMapper;
import com.coopcredit.creddit_application_service.infrastructure.web.dto.affiliates.AffiliateResponse;
import com.coopcredit.creddit_application_service.infrastructure.web.dto.affiliates.CreateAffiliateRequest;
import com.coopcredit.creddit_application_service.infrastructure.web.response.AppResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/affiliates")
@Tag(name = "Affiliates", description = "Affiliate management endpoints")
@SecurityRequirement(name = "bearer-jwt")
public class AffiliateController {

    private final RegisterAffiliateUseCase registerAffiliateUseCase;
    private final GetAffiliateUseCase getAffiliateUseCase;
    private final UpdateAffiliateUseCase updateAffiliateUseCase;
    private final AffiliateDtoMapper dtoMapper;

    public AffiliateController(
            RegisterAffiliateUseCase registerAffiliateUseCase,
            GetAffiliateUseCase getAffiliateUseCase,
            UpdateAffiliateUseCase updateAffiliateUseCase,
            AffiliateDtoMapper dtoMapper) {
        this.registerAffiliateUseCase = registerAffiliateUseCase;
        this.getAffiliateUseCase = getAffiliateUseCase;
        this.updateAffiliateUseCase = updateAffiliateUseCase;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new affiliate (Admin only)")
    public ResponseEntity<AppResponse<AffiliateResponse>> createAffiliate(
            @Valid @RequestBody CreateAffiliateRequest request) {

        Affiliate affiliate = dtoMapper.toDomain(request);
        Affiliate created = registerAffiliateUseCase.execute(affiliate);
        AffiliateResponse response = dtoMapper.toResponse(created);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AppResponse.success("Affiliate created successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AFILIADO', 'ANALISTA', 'ADMIN')")
    @Operation(summary = "Get affiliate by ID")
    public ResponseEntity<AppResponse<AffiliateResponse>> getAffiliate(@PathVariable Long id) {
        Affiliate affiliate = getAffiliateUseCase.execute(id);
        AffiliateResponse response = dtoMapper.toResponse(affiliate);

        return ResponseEntity.ok(AppResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update affiliate (Admin only)")
    public ResponseEntity<AppResponse<AffiliateResponse>> updateAffiliate(
            @PathVariable Long id,
            @Valid @RequestBody CreateAffiliateRequest request) {

        Affiliate updateData = dtoMapper.toDomain(request);
        Affiliate updated = updateAffiliateUseCase.execute(id, updateData);
        AffiliateResponse response = dtoMapper.toResponse(updated);

        return ResponseEntity.ok(AppResponse.success("Affiliate updated successfully", response));
    }
}
