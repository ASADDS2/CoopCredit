package com.coopcredit.risk_central_mock_service.infrastructure.controllers;

import com.coopcredit.risk_central_mock_service.domain.model.RiskEvaluation;
import com.coopcredit.risk_central_mock_service.domain.ports.in.CalculateRiskUseCase;
import com.coopcredit.risk_central_mock_service.infrastructure.web.dto.RiskEvaluationRequest;
import com.coopcredit.risk_central_mock_service.infrastructure.web.dto.RiskEvaluationResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for risk evaluation endpoints
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class RiskEvaluationController {

        private final CalculateRiskUseCase calculateRiskUseCase;

        @PostMapping("/risk-evaluation")
        public ResponseEntity<RiskEvaluationResponse> evaluateRisk(
                        @Valid @RequestBody RiskEvaluationRequest request) {

                log.info("Evaluating risk for document: {}, amount: {}, term: {}",
                                request.getDocumentId(), request.getAmount(), request.getTerm());

                // Execute use case
                RiskEvaluation evaluation = calculateRiskUseCase.execute(
                                request.getDocumentId(),
                                request.getAmount(),
                                request.getTerm());

                // Map to response DTO
                RiskEvaluationResponse response = RiskEvaluationResponse.fromDomain(evaluation);

                log.info("Risk evaluation completed - Score: {}, Level: {}",
                                response.getScore(), response.getRiskLevel());

                return ResponseEntity.ok(response);
        }

        @GetMapping("/health")
        public ResponseEntity<String> health() {
                return ResponseEntity.ok("Service is running");
        }
}
